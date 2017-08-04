package org.yagel.monitor.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.yagel.monitor.Resource;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.AggregatedStatus;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatedStatusDAO extends AbstractTimeRangeDAO {
  public AggregatedStatusDAO(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
  }


  public synchronized List<AggregatedResourceStatus> getAggregatedStatuses(String environmentName, Date from, Date to) {

    Map<Resource, Map<Status, Integer>> aggregatedMap = new HashMap<>();

    List<Date[]> dateFrames = DataUtils.splitDatesIntoMonths(from, to);

    for (Date[] dates : dateFrames) {

      switchCollection(dates[0]);

      AggregateIterable<Document> documents = thisCollection.aggregate(Arrays.asList(
          Aggregates.match(new Document("environmentName", environmentName).append("updated", new Document("$gte", dates[0]).append("$lte", dates[1]))),
          Document.parse("{$group: {'_id':{ 'res': '$resource','status':'$statusOrdinal'},'count':{ '$sum' :1}}}"),
          Document.parse("{$group: {'_id':{ 'resource': '$_id.res'},'statuses': {'$push': {'statusOrdinal':'$_id.status', 'count': '$count'}}, 'count':{'$sum':'$count'}}}")

      ));

      for (Document doc : documents) {
        Document resourceDoc = (Document) ((Document) doc.get("_id")).get("resource");
        Resource resource = DocumentMapper.resourceFromStatusRef(resourceDoc);

        List<Document> statsListDoc = (List<Document>) doc.get("statuses");

        Map<Status, Integer> statusMap = new HashMap<>();

        for (Document statusDoc : statsListDoc) {
          statusMap.put(Status.fromSerialNumber(statusDoc.getInteger("statusOrdinal")), statusDoc.getInteger("count"));
        }

        if (!aggregatedMap.containsKey(resource))
          aggregatedMap.put(resource, statusMap);
        else {
          Map<Status, Integer> existStatusMap = aggregatedMap.get(resource);
          Map<Status, Integer> resultingMap = Stream.concat(statusMap.entrySet().stream(), existStatusMap.entrySet().stream())
              .collect(Collectors.groupingBy(Map.Entry::getKey,
                  Collectors.summingInt(Map.Entry::getValue)));
          aggregatedMap.put(resource, resultingMap);

        }
      }
    }

    List<AggregatedResourceStatus> aggregatedResourceStatuses = new ArrayList<>();
    for (Map.Entry<Resource, Map<Status, Integer>> enrty : aggregatedMap.entrySet()) {
      AggregatedResourceStatus aggregatedResourceStatus = new AggregatedResourceStatus();
      aggregatedResourceStatus.setResource(enrty.getKey());

      long totalcount = 0;
      List<AggregatedStatus> aggregatedStatuses = new ArrayList<>();
      for (Map.Entry<Status, Integer> statusEntry : enrty.getValue().entrySet()) {
        AggregatedStatus aggregatedStatus = new AggregatedStatus();
        aggregatedStatus.setStatus(statusEntry.getKey());
        aggregatedStatus.setCount(statusEntry.getValue());
        aggregatedStatuses.add(aggregatedStatus);
        totalcount += statusEntry.getValue();
      }

      aggregatedResourceStatus.setResourceStatuses(aggregatedStatuses);
      aggregatedResourceStatus.setCount(totalcount);
      aggregatedResourceStatuses.add(aggregatedResourceStatus);
    }

    return aggregatedResourceStatuses;

  }

  // todo consider month switch during data extraction
  public synchronized long getStatusCount(String environmentName, String resourceId, Status status, Date from, Date to) {
    switchCollection(from);

    return thisCollection.count(Filters.and(
        Filters.eq("statusOrdinal", status.getSeriaNumber()),
        Filters.eq("resource.resourceId", resourceId),
        Filters.eq("environmentName", environmentName),
        Filters.gte("updated", from),
        Filters.lte("updated", to)));


  }
}

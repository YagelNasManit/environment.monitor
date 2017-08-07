package org.yagel.monitor.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.AggregatedStatus;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatedStatusDAO extends AbstractTimeRangeDAO {
  public AggregatedStatusDAO(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
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


  public synchronized List<AggregatedResourceStatus> getAggregatedStatuses(String environmentName, Date from, Date to) {
    List<AggregatedResourceStatus> aggStatusesResulting = new ArrayList<>();

    List<Date[]> dateFrames = DataUtils.splitDatesIntoMonths(from, to);

    for (Date[] dates : dateFrames) {
      switchCollection(dates[0]);

      AggregateIterable<Document> documents = thisCollection.aggregate(Arrays.asList(
          Aggregates.match(new Document("environmentName", environmentName).append("updated", new Document("$gte", dates[0]).append("$lte", dates[1]))),
          Document.parse("{$group: {'_id':{ 'res': '$resource','status':'$statusOrdinal'},'count':{ '$sum' :1}}}"),
          Document.parse("{$group: {'_id':{ 'resource': '$_id.res'},'statuses': {'$push': {'statusOrdinal':'$_id.status', 'count': '$count'}}, 'count':{'$sum':'$count'}}}")

      ));

      List<AggregatedResourceStatus> aggStatuses = documents.map(DocumentMapper::aggregatedResourceStatusFromDocument).into(new ArrayList<>());
      aggStatusesResulting.addAll(aggStatuses);
    }

    return buildAggregatedList(aggStatusesResulting);

  }

  private List<AggregatedResourceStatus> buildAggregatedList(List<AggregatedResourceStatus> aggStatuses) {
    Collection<AggregatedResourceStatus> values = aggStatuses.stream()
        .collect(
            Collectors.toMap(
                AggregatedResourceStatus::getResource,
                Function.identity(),
                this::mergeResourceStatuses
            ))
        .values();

    return new ArrayList<>(values);
  }

  private AggregatedResourceStatus mergeResourceStatuses(AggregatedResourceStatus first, AggregatedResourceStatus second) {
    Collection<AggregatedStatus> statuses = Stream.concat(
        first.getResourceStatuses().stream(),
        second.getResourceStatuses().stream()
    )
        .collect(Collectors.toMap(
            AggregatedStatus::getStatus,
            Function.identity(),
            this::mergeAggregatedStatuses
        ))
        .values();

    first.setResourceStatuses(new ArrayList<>(statuses));
    first.setCount(first.getCount() + second.getCount());

    return first;
  }

  private AggregatedStatus mergeAggregatedStatuses(AggregatedStatus first, AggregatedStatus second) {
    System.out.println(first + " " + second);
    first.setCount(first.getCount() + second.getCount());
    return first;
  }


}

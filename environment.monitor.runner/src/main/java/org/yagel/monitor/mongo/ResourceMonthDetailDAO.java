package org.yagel.monitor.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.utils.DataUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class ResourceMonthDetailDAO {

  private final static String COLLECTION_NAME = "ResourceMonthDetail%s";
  private MongoDatabase mongoDatabase;
  private MongoCollection<Document> thisCollection;
  private int thisDate = -1;

  public ResourceMonthDetailDAO(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
    switchCollection(new Date());
  }

  public synchronized void insert(String environmentName, ResourceStatus resourceStatus) {
    switchCollection(resourceStatus.getUpdated());
    thisCollection.insertOne(DocumentMapper.resourceStatusToDocument(environmentName, resourceStatus));
  }

  public synchronized void insert(final String environmentName, final Collection<ResourceStatus> resourcesStatus) {
    resourcesStatus.forEach(rs -> insert(environmentName, rs));
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

    LocalDateTime fromDateTime = DataUtils.asLocalDateTime(from);
    LocalDateTime toDateTime = DataUtils.asLocalDateTime(from);


    List<AggregatedResourceStatus> aggStatuses = new ArrayList<>();

    while (fromDateTime.isBefore(toDateTime) || fromDateTime.isEqual(toDateTime)) {

      switchCollection(DataUtils.asDate(fromDateTime));

      AggregateIterable<Document> documents = thisCollection.aggregate(Arrays.asList(
          Aggregates.match(new Document("environmentName", environmentName).append("updated", new Document("$gte", from).append("$lte", to))),
          Document.parse("{$group: {'_id':{ 'res': '$resource','status':'$statusOrdinal'},'count':{ '$sum' :1}}}"),
          Document.parse("{$group: {'_id':{ 'resource': '$_id.res'},'statuses': {'$push': {'statusOrdinal':'$_id.status', 'count': '$count'}}, 'count':{'$sum':'$count'}}}")

      ));

      // TODO serialise statuses with zero value also!
      List<AggregatedResourceStatus> monthAggStatuses = documents
          .map(DocumentMapper::aggregatedResourceStatusFromDocument)
          .into(new ArrayList<>());

      aggStatuses.addAll(monthAggStatuses);

      fromDateTime = fromDateTime.plusMonths(1);

    }

    return aggStatuses;

  }

  // todo consider month switch during data extraction
  public List<ResourceStatus> getStatuses(String environmentName, String resourceId, Date from, Date to) {
    switchCollection(from);

    return thisCollection.find(Filters.and(
        Filters.eq("environmentName", environmentName),
        Filters.gte("updated", from),
        Filters.lte("updated", to),
        Filters.eq("resource.resourceId", resourceId))
    )
        .sort(Sorts.ascending("updated"))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

  }


  private void switchCollection(Date date) {
    int toDate = DataUtils.joinYearMonthValues(date);
    if (toDate == thisDate)
      return;

    String collectionName = String.format(COLLECTION_NAME, toDate);
    thisCollection = mongoDatabase.getCollection(collectionName);
  }
}

package org.yagel.monitor.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceMonthDetailDAO {

  private final static String COLLECTION_NAME = "ResourceMonthDetail%s";
  private MongoDatabase mongoDatabase;
  private MongoCollection<Document> thisCollection;
  private int thisDate = -1;

  public ResourceMonthDetailDAO(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
    switchCollection(new Date());
  }

  private void switchCollection(Date date) {
    int toDate = DataUtils.joinYearMonthValues(date);
    if (toDate == thisDate)
      return;

    String collectionName = String.format(COLLECTION_NAME, toDate);

    boolean collectionExists = mongoDatabase.listCollectionNames().into(new ArrayList<>()).contains(collectionName);

    thisCollection = mongoDatabase.getCollection(collectionName);

    /*if(collectionExists)
      thisCollection.ensureIndex("{environmentName: 1, resourceOrdinal:1, updated:1}");*/
  }

  public synchronized void insert(String environmentName, ResourceStatus resourceStatus) {
    //ResourceMonthDetail r = new ResourceMonthDetail(environmentName, resource);
    switchCollection(resourceStatus.getUpdated());
    thisCollection.insertOne(DocumentMapper.resourceStatusToDocument(environmentName, resourceStatus));
  }

  public synchronized void insert(String environmentName, Collection<ResourceStatus> resourcesStatus) {
    for (ResourceStatus status : resourcesStatus) {
      insert(environmentName, status);
    }

  }

  public synchronized long getStatusCount(String environmentName, String resourceId, Status status, Date from, Date to) {
    //int toDate = DataUtils.joinYearMonthValues(from);
    switchCollection(from);


    return thisCollection.count(Filters.and(
        Filters.eq("statusOrdinal", status.getSeriaNumber()),
        Filters.eq("resourceId", resourceId),
        Filters.eq("environmentName", environmentName),
        Filters.gte("updated", from),
        Filters.lte("updated", to)));


  }

  public synchronized Map<String, Map<Status, Integer>> getAggregatedStatuses(String environmentName, Date from, Date to) {
    switchCollection(from);
    AggregateIterable<Document> documents = thisCollection.aggregate(Arrays.asList(
        Aggregates.match(new Document("environmentName", environmentName).append("updated", new Document("$gte", from).append("$lte", to))),
        Document.parse("{$group: {'_id':{ 'resId': '$resourceId','status':'$statusOrdinal'},'total':{ '$sum' :1}}}"),
        Document.parse("{$group: {'_id':'$_id.resId','statuses': {'$push': {'statusOrdinal':'$_id.status', 'total': '$total'}}}}")

    ));



    Map<String, Map<Status, Integer>> aggStatuses = new HashMap<>();

    for (Document document : documents) {
      aggStatuses.put(document.getString("_id").toString(), DocumentMapper.aggregatedResourceStatusFromDocument(document));
    }

    return aggStatuses;

  }


  public List<ResourceStatus> getStatuses(String environmentName, String resourceId, Date from, Date to) {
    switchCollection(from);

    return thisCollection.find(Filters.and(
        Filters.eq("environmentName", environmentName),
        Filters.gte("updated", from),
        Filters.lte("updated", to),
        Filters.eq("resourceId", resourceId))
    )
        .sort(Sorts.ascending("updated"))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

  }
}

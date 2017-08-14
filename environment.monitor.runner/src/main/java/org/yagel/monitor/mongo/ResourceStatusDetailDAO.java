package org.yagel.monitor.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static org.yagel.monitor.mongo.DocumentMapper.resourceStatusToDocument;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.StatusUpdate;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.resource.StatusUpdateImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class ResourceStatusDetailDAO extends AbstractTimeRangeDAO {

  public ResourceStatusDetailDAO(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
    switchCollection(new Date());
  }


  /**
   * Inserts new resource status into corresponding month collection
   *
   * @param environmentName environment resource belongs to
   * @param resourceStatus  status to be inserted
   */
  public synchronized void insert(String environmentName, ResourceStatus resourceStatus) {
    switchCollection(resourceStatus.getUpdated());
    thisCollection.insertOne(resourceStatusToDocument(environmentName, resourceStatus));
  }


  /**
   * Inserts multiple resource statuses into corresponding month collection
   * @param environmentName
   * @param resourcesStatus
   */
  public synchronized void insert(final String environmentName, final Collection<ResourceStatus> resourcesStatus) {
    resourcesStatus.forEach(rs -> {
      switchCollection(rs.getUpdated());
      insert(environmentName, rs);
    });
  }


  @Deprecated
  public List<ResourceStatus> getStatuses(String environmentName, String resourceId, Date from, Date to) {
    // consider month switch during data extraction if will be used
    switchCollection(from);

    return thisCollection.find(and(
        eq("environmentName", environmentName),
        gte("updated", from),
        lte("updated", to),
        eq("resource.resourceId", resourceId))
    )
        .sort(Sorts.ascending("updated"))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

  }

  /**
   * Fetch time scale statuses for particular resource
   * @param environmentName environment that resource belongs to
   * @param resourceId resource id to fetch statuses
   * @param from start date for statuses fetching
   * @param to end date for statuses fetching
   * @return
   */
  public List<StatusUpdate> getStatusUpdates(String environmentName, String resourceId, Date from, Date to) {
    // todo definitelly need months iteration
    switchCollection(from);

    Bson filter = and(
        eq("environmentName", environmentName),
        eq("resource.resourceId", resourceId),
        gte("updated", from),
        lte("updated", to)
    );

    Bson project = fields(include("updated", "statusOrdinal"), excludeId());

    return this.thisCollection
        .find(filter)
        .projection(project)
        .map(
            doc -> new StatusUpdateImpl(
                Status.fromSerialNumber(doc.getInteger("statusOrdinal")),
                doc.getDate("updated")
            )
        )
        .into(new ArrayList<>());


  }
}

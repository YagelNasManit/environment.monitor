package org.yagel.monitor.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
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


public class ResourceMonthDetailDAO extends AbstractTimeRangeDAO {

  public ResourceMonthDetailDAO(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
    switchCollection(new Date());
  }

  public synchronized void insert(String environmentName, ResourceStatus resourceStatus) {
    switchCollection(resourceStatus.getUpdated());
    thisCollection.insertOne(DocumentMapper.resourceStatusToDocument(environmentName, resourceStatus));
  }

  public synchronized void insert(final String environmentName, final Collection<ResourceStatus> resourcesStatus) {
    resourcesStatus.forEach(rs -> {
      switchCollection(rs.getUpdated());
      insert(environmentName, rs);
    });
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

  public List<StatusUpdate> getStatusUpdates(String environmentName, String resourceId, Date from, Date to) {

    switchCollection(from);

    Bson filter = Filters.and(
        Filters.eq("environmentName", environmentName),
        Filters.eq("resource.resourceId", resourceId),
        Filters.gte("updated", from),
        Filters.lte("updated", to)
    );

    Bson project = Projections.fields(Projections.include("updated", "statusOrdinal"), Projections.excludeId());

    return this.thisCollection
        .find(filter)
        .projection(project)
        .map(
            doc -> new StatusUpdateImpl(Status.fromSerialNumber(doc.getInteger("statusOrdinal")), doc.getDate("updated"))
        )
        .into(new ArrayList<>());


  }
}

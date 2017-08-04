package org.yagel.monitor.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.yagel.monitor.ResourceStatus;

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
}

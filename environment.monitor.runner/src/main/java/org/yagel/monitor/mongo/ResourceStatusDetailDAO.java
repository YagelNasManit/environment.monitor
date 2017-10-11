package org.yagel.monitor.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static org.yagel.monitor.mongo.DocumentMapper.resourceStatusToDocument;

import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.StatusUpdate;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.resource.StatusUpdateImpl;
import org.yagel.monitor.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class ResourceStatusDetailDAO extends AbstractTimeRangeDAO {

  public ResourceStatusDetailDAO(MongoConnect connect) {
    super(connect);
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
   *
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

  public List<StatusUpdate> getStatusUpdatesDetailed(String environmentName, String resourceId, Date from, Date to) {
    return getStatusUpdates(environmentName, resourceId, from, to, true);
  }

  public List<StatusUpdate> getStatusUpdatesShort(String environmentName, String resourceId, Date from, Date to) {
    return getStatusUpdates(environmentName, resourceId, from, to, false);
  }

  /**
   * Fetch time scale statuses for particular resource
   *
   * @param environmentName environment that resource belongs to
   * @param resourceId      resource id to fetch statuses
   * @param from            start date for statuses fetching
   * @param to              end date for statuses fetching
   * @return
   */
  private List<StatusUpdate> getStatusUpdates(String environmentName, String resourceId, Date from, Date to,
      boolean fetchStatusDetails) {
    List<Date[]> dateFrames = DataUtils.splitDatesIntoMonths(from, to);
    List<StatusUpdate> updates = new ArrayList<>();

    for (Date[] dates : dateFrames) {
      switchCollection(dates[0]);

      Bson filter = and(
          eq("environmentName", environmentName),
          eq("resource.resourceId", resourceId),
          gte("updated", dates[0]),
          lte("updated", dates[1])
      );


      List<String> fieldsToInclude = new ArrayList<>();
      fieldsToInclude.add("updated");
      fieldsToInclude.add("statusOrdinal");

      if (fetchStatusDetails)
        fieldsToInclude.add("statusDetails");

      Bson project = fields(include(fieldsToInclude), excludeId());

      List<StatusUpdate> monthlyUpdates = this.thisCollection
          .find(filter)
          .projection(project)
          .map(
              doc -> new StatusUpdateImpl(
                  Status.fromSerialNumber(doc.getInteger("statusOrdinal")),
                  doc.getDate("updated"),
                  doc.getString("statusDetails")
              )
          )
          .into(new ArrayList<>());

      updates.addAll(monthlyUpdates);
    }
    return updates;

  }


}

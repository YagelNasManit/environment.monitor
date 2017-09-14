package org.yagel.monitor.mongo;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregatedStatusDAO extends AbstractTimeRangeDAO {

  public AggregatedStatusDAO(MongoConnect connect) {
    super(connect);
  }


  @Deprecated
  public synchronized long getStatusCount(String environmentName, String resourceId, Status status, Date from, Date to) {
    // todo consider month switch during data extraction, if will be used
    switchCollection(from);

    return thisCollection.count(and(
        eq("statusOrdinal", status.getSeriaNumber()),
        eq("resource.resourceId", resourceId),
        eq("environmentName", environmentName),
        gte("updated", from),
        lte("updated", to)));


  }


  /**
   * Extracts aggregated statuses for environment resources over time
   *
   * @param environmentName environment that resources belongs to
   * @param resourceIds     resource id`s to fetch aggregated statuses
   * @param from            start date frame
   * @param to              end date frame
   * @return
   */
  public synchronized List<AggregatedResourceStatus> getAggregatedStatuses(String environmentName, Set<String> resourceIds,
      Date from, Date to) {
    List<AggregatedResourceStatus> aggStatusesResulting = new ArrayList<>();

    // build start and end time frames for each month in date range
    List<Date[]> dateFrames = DataUtils.splitDatesIntoMonths(from, to);

    // iterate over each time frame and extract aggregated results
    for (Date[] dates : dateFrames) {
      switchCollection(dates[0]);


      // leaving creation of aggregation pipeline as is without fancy stuff, just to  keep readable
      Document searchQuery = new Document("environmentName", environmentName).append("updated", new Document("$gte", dates[0]).append("$lte", dates[1]));
      if (resourceIds != null)
        searchQuery.append("resource.resourceId", new Document("$in", resourceIds));

      //aggregate statuses
      AggregateIterable<Document> documents = thisCollection.aggregate(Arrays.asList(
          match(searchQuery),
          Document.parse("{$group: {'_id':{ 'res': '$resource','status':'$statusOrdinal'},'count':{ '$sum' :1}}}"),
          Document.parse("{$group: {'_id':{ 'resource': '$_id.res'},'statuses': {'$push': {'statusOrdinal':'$_id.status', 'count': '$count'}}, 'count':{'$sum':'$count'}}}")

      ));

      // map into aggregated statuses POJO
      List<AggregatedResourceStatus> aggStatuses = documents.map(DocumentMapper::aggregatedResourceStatusFromDocument).into(new ArrayList<>());
      aggStatusesResulting.addAll(aggStatuses);
    }

    // now merging aggregated statuses for each month
    return buildAggregatedResourceList(aggStatusesResulting);
  }


  /**
   * Extracts aggregated statuses for all environment resources over time
   * @param environmentName environment to fetch resource statuses
   * @param from start date frame
   * @param to end date frame
   * @return
   */
  public synchronized List<AggregatedResourceStatus> getAggregatedStatuses(String environmentName, Date from, Date to) {
    return getAggregatedStatuses(environmentName, null, from, to);
  }


  /**
   * Merges {@link AggregatedResourceStatus} list.
   * Method takes aggregated statuses for each month and does summing of corresponding status values for corresponding resources.
   *
   * @param aggStatuses
   * @return
   */
  private List<AggregatedResourceStatus> buildAggregatedResourceList(List<AggregatedResourceStatus> aggStatuses) {
    Collection<AggregatedResourceStatus> values = aggStatuses.stream()
        .collect(
            Collectors.toMap(
                AggregatedResourceStatus::getResource,
                Function.identity(),
                this::mergeAggregatedResourceStatuses
            ))
        .values();

    return new ArrayList<>(values);
  }


  private AggregatedResourceStatus mergeAggregatedResourceStatuses(AggregatedResourceStatus first, AggregatedResourceStatus second) {
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

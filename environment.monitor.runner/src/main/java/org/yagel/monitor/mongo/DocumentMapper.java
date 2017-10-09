package org.yagel.monitor.mongo;

import org.bson.Document;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.AggregatedStatus;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentMapper {

  private static final String DOC_ID_KEY = "_id";

  // env
  private static final String ENV_MANE_KEY = "environmentName";

  // resource
  private static final String RES_NAME_KEY = "name";

  // status
  private static final String STATUS_ORDINAL_KEY = "statusOrdinal";
  private static final String STATUS_UPDATED_KEY = "updated";
  private static final String STATUS_RES_REF_KEY = "resource";
  private static final String STATUS_RES_NAME_KEY = "resourceName";
  private static final String STATUS_RES_ID_KEY = "resourceId";
  private static final String STATUS_RES_DETAILS_KEY = "statusDetails";

  //aggregated status
  private static final String AGG_COUNT_KEY = "count";
  private static final String AGG_STATUSES_KEY = "statuses";


  private DocumentMapper() {
  }

  public static Document resourceStatusToDocument(String evnName, ResourceStatus resourceStatus) {
    return new Document()
        .append(ENV_MANE_KEY, evnName)
        .append(STATUS_RES_REF_KEY, resourceToStatusRef(resourceStatus.getResource()))
        .append(STATUS_ORDINAL_KEY, resourceStatus.getStatus().getSeriaNumber())
        .append(STATUS_UPDATED_KEY, resourceStatus.getUpdated())
        .append(STATUS_RES_DETAILS_KEY, resourceStatus.getStatusDetails());
  }

  public static ResourceStatus resourceStatusFromDocument(Document document) {
    Document resourceDoc = (Document) document.get(STATUS_RES_REF_KEY);
    Resource resource = resourceFromStatusRef(resourceDoc);

    Status status = Status.fromSerialNumber(document.getInteger(STATUS_ORDINAL_KEY));
    Date updated = document.getDate(STATUS_UPDATED_KEY);
    String details = document.getString(STATUS_RES_DETAILS_KEY);
    return new ResourceStatusImpl(resource, status, updated,details);

  }

  public static AggregatedResourceStatus aggregatedResourceStatusFromDocument(Document document) {
    Document id = (Document) document.get(DOC_ID_KEY);
    Resource resource = resourceFromStatusRef((Document) id.get(STATUS_RES_REF_KEY));

    long totalCount = document.getInteger(AGG_COUNT_KEY);

    List<Document> statuses = (List<Document>) document.get(AGG_STATUSES_KEY);

    List<AggregatedStatus> aggregatedStatuses = statuses
        .stream()
        .map(DocumentMapper::aggregatedStatusFromDocument)
        .collect(Collectors.toList());

    AggregatedResourceStatus status = new AggregatedResourceStatus();
    status.setCount(totalCount);
    status.setResource(resource);
    status.setResourceStatuses(aggregatedStatuses);


    return status;
  }

  public static Resource resourceFromStatusRef(Document document) {
    return new ResourceImpl(document.getString(STATUS_RES_ID_KEY), document.getString(STATUS_RES_NAME_KEY));
  }

  public static Document resourceToStatusRef(Resource resource) {
    return new Document(STATUS_RES_ID_KEY, resource.getId()).append(STATUS_RES_NAME_KEY, resource.getName());
  }

  public static Document resourceToDocument(Resource resource) {
    return new Document(DOC_ID_KEY, resource.getId()).append(RES_NAME_KEY, resource.getName());
  }

  public static Resource resourceFromDocument(Document document) {
    return new ResourceImpl(document.getString(DOC_ID_KEY), document.getString(RES_NAME_KEY));
  }

  private static AggregatedStatus aggregatedStatusFromDocument(Document document) {

    Status status = Status.fromSerialNumber(document.getInteger(STATUS_ORDINAL_KEY));
    long count = document.getInteger(AGG_COUNT_KEY);

    AggregatedStatus aggregatedStatus = new AggregatedStatus();
    aggregatedStatus.setStatus(status);
    aggregatedStatus.setCount(count);

    return aggregatedStatus;
  }


}

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

  public static Document resourceStatusToDocument(String evnName, ResourceStatus resourceStatus) {
    return new Document()
        .append("environmentName", evnName)
        .append("resource", resourceToStatusRef(resourceStatus.getResource()))
        .append("statusOrdinal", resourceStatus.getStatus().getSeriaNumber())
        .append("updated", resourceStatus.getUpdated());
  }

  public static ResourceStatus resourceStatusFromDocument(Document document) {

    Document resourceDoc = (Document) document.get("resource");
    Resource resource = resourceFromStatusRef(resourceDoc);


    Status status = Status.fromSerialNumber(document.getInteger("statusOrdinal"));
    Date updated = document.getDate("updated");
    return new ResourceStatusImpl(resource, status, updated);

  }


  public static AggregatedResourceStatus aggregatedResourceStatusFromDocument(Document document) {
    Document id = (Document) document.get("_id");
    Resource resource = resourceFromStatusRef((Document) id.get("resource"));
    long totalCount = document.getInteger("count");

    List<Document> statuses = (List<Document>) document.get("statuses");

    List<AggregatedStatus> aggregatedStatuses = statuses.stream().map(DocumentMapper::aggregatedStatusFromDocument).collect(Collectors.toList());

    AggregatedResourceStatus status = new AggregatedResourceStatus();
    status.setCount(totalCount);
    status.setResource(resource);
    status.setResourceStatuses(aggregatedStatuses);


    return status;
  }


  public static Resource resourceFromStatusRef(Document document) {
    return new ResourceImpl(document.getString("resourceId"), document.getString("resourceName"));
  }

  public static Document resourceToStatusRef(Resource resource) {
    return new Document("resourceId", resource.getId()).append("resourceName", resource.getName());
  }

  private static AggregatedStatus aggregatedStatusFromDocument(Document document) {

    Status status = Status.fromSerialNumber(document.getInteger("statusOrdinal"));
    long count = document.getInteger("count");

    AggregatedStatus aggregatedStatus = new AggregatedStatus();
    aggregatedStatus.setStatus(status);
    aggregatedStatus.setCount(count);

    return aggregatedStatus;
  }

}

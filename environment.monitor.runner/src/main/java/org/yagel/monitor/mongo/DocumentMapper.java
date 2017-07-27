package org.yagel.monitor.mongo;

import org.bson.Document;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


  public static Map<Status, Integer> aggregatedResourceStatusFromDocument(Document document) {
    Map<Status, Integer> statusMap = new HashMap<>();

    List<Document> list = (List<Document>) document.get("statuses");
    for (Document status : list) {
      statusMap.put(Status.fromSerialNumber((status.getInteger("statusOrdinal"))), status.getInteger("total"));
    }

    return statusMap;
  }


  public static Resource resourceFromStatusRef(Document document) {
    return new ResourceImpl(document.getString("resourceId"), document.getString("resourceName"));
  }

  public static Document resourceToStatusRef(Resource resource) {
    return new Document("resourceId", resource.getId()).append("resourceName", resource.getName());
  }

}

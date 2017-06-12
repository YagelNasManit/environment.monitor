package org.yagel.monitor.api.rest;


import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status/{environmentName}")
public class EnvironmentStatusService {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public EnvironmentStatus getStatus(@PathParam("environmentName") String environmentName) {
    List<ResourceStatus> resourceStatuses = MongoConnector.getInstance().getLastStatusDAO().find(environmentName);
    EnvironmentStatus environmentStatus = new EnvironmentStatus(environmentName, resourceStatuses);
    return environmentStatus;
  }
}

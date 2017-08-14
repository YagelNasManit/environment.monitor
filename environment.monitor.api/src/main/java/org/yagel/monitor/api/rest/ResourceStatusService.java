package org.yagel.monitor.api.rest;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.StatusUpdate;
import org.yagel.monitor.mongo.MongoConnector;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/resource/status/")
public class ResourceStatusService extends AbstractService {


  @RequestMapping(value = "{environmentName}/{resourceId}", method = RequestMethod.GET)
  public ResponseEntity<List<StatusUpdate>> getResourceStatuses(
      @PathVariable("environmentName") String environmentName,
      @PathVariable(value = "resourceId") String resourceId,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {

    List<StatusUpdate> updateList = MongoConnector.getInstance().getMonthDetailDAO().getStatusUpdates(environmentName, resourceId, startDate, endDate);

    return ResponseEntity.ok(updateList);

  }
}

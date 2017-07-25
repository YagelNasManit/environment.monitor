package org.yagel.monitor.api.rest;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;

import java.util.List;


@RestController
@RequestMapping("/")
public class EnvironmentStatusService {


  @RequestMapping(value = "current/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<EnvironmentStatus> getStatus(@PathVariable("environmentName") String environmentName) {
    List<ResourceStatus> resourceStatuses = MongoConnector.getInstance().getLastStatusDAO().find(environmentName);
    EnvironmentStatus environmentStatus = new EnvironmentStatus(environmentName, resourceStatuses);
    return ResponseEntity.ok(environmentStatus);
  }


 /* // TODO fix return
  @RequestMapping(value = "aggregated/{environmentName}", method = RequestMethod.GET)
  public Map<String, Map<Status, Integer>> getStatus(
      @PathVariable("environmentName") String environmentName,
      @RequestParam("startDate") @DateTimeFormat(pattern="yyyy-MM-dd-hh-ss") Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern="yyyy-MM-dd-hh-ss") Date endDate) {
    ResourceMonthDetailDAO detailDAO = MongoConnector.getInstance().getMonthDetailDAO();

    Map<String, Map<Status, Integer>> aggStatusses = detailDAO.getAggregatedStatuses(environmentName,startDate,endDate);

    return aggStatusses;
  }*/

 /* @RequestMapping(value = "period/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<EnvironmentStatus> getPeriodStatus(
      @PathVariable("environmentName") String environmentName,
      @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd-hh-ss") Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd-hh-ss") Date endDate) {
    ResourceMonthDetailDAO detailDAO = MongoConnector.getInstance().getMonthDetailDAO();

  }*/
}

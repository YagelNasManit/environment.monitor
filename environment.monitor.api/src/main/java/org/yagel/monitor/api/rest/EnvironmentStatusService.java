package org.yagel.monitor.api.rest;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class EnvironmentStatusService {


  @RequestMapping(value = "current/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<EnvironmentStatus> getEnvironmentStatus(@PathVariable("environmentName") String environmentName) {
    List<ResourceStatus> resourceStatuses = MongoConnector.getInstance().getLastStatusDAO().find(environmentName);
    EnvironmentStatus environmentStatus = new EnvironmentStatus(environmentName, resourceStatuses);
    return ResponseEntity.ok(environmentStatus);
  }


  @RequestMapping(value = "current", method = RequestMethod.GET)
  public ResponseEntity<List<EnvironmentStatus>> getOverallStatus() {

    List<String> envs = ScheduleRunnerImpl.getInstance().getConfig().getEnvironments()
        .stream()
        .map(EnvironmentConfig::getEnvName)
        .collect(Collectors.toList());

    ResourceLastStatusDAO statusDAO = MongoConnector.getInstance().getLastStatusDAO();

    List<EnvironmentStatus> statusList = new ArrayList<>();

    for (String env : envs) {
      statusList.add(new EnvironmentStatus(env, statusDAO.find(env)));
    }

    return ResponseEntity.ok(statusList);
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

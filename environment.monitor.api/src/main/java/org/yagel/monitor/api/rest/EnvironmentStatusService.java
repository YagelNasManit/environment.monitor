package org.yagel.monitor.api.rest;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.mongo.AggregatedStatusDAO;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.resource.AggregatedResourceStatus;

import java.util.Date;
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

    final ResourceLastStatusDAO statusDAO = MongoConnector.getInstance().getLastStatusDAO();


    List<EnvironmentStatus> statusList = envs
        .stream()
        .map(env -> new EnvironmentStatus(env, statusDAO.find(env)))
        .collect(Collectors.toList());

    return ResponseEntity.ok(statusList);
  }


  @RequestMapping(value = "aggregated/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<List<AggregatedResourceStatus>> getStatus(
      @PathVariable("environmentName") String environmentName,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {

    AggregatedStatusDAO detailDAO = MongoConnector.getInstance().getAggregatedStatusDAO();
    List<AggregatedResourceStatus> aggStatusses = detailDAO.getAggregatedStatuses(environmentName, startDate, endDate);

    return ResponseEntity.ok(aggStatusses);
  }
}

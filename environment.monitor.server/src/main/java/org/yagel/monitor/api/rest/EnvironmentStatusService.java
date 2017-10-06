package org.yagel.monitor.api.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.ScheduleRunner;
import org.yagel.monitor.api.rest.dto.EnvironmentStatusDTO;
import org.yagel.monitor.mongo.AggregatedStatusDAO;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/environment/status/")
public class EnvironmentStatusService extends AbstractService {

  @Autowired
  private ResourceLastStatusDAO lastStatusDAO;

  @Autowired
  private AggregatedStatusDAO aggregatedStatusDAO;

  @Autowired
  ScheduleRunner runner;

  @RequestMapping(value = "current/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<EnvironmentStatusDTO> getEnvironmentStatus(@PathVariable("environmentName") String environmentName) {
    List<ResourceStatus> resourceStatuses = lastStatusDAO.find(environmentName);

    EnvironmentStatusDTO environmentStatus = new EnvironmentStatusDTO(environmentName, resourceStatuses);
    environmentStatus.setOverallStatus(getOverallStatus(resourceStatuses));
    return ResponseEntity.ok(environmentStatus);
  }


  @RequestMapping(value = "current", method = RequestMethod.GET)
  public ResponseEntity<List<EnvironmentStatusDTO>> getOverallStatus() {

    List<String> envs = runner.getConfig().getEnvironments()
        .stream()
        .map(EnvironmentConfig::getEnvName)
        .collect(Collectors.toList());



    List<EnvironmentStatusDTO> statusList = envs
        .stream()
        .sorted(String::compareTo)
        .map(env -> {
          List<ResourceStatus> statuses = lastStatusDAO.find(env)
              .stream()
              .sorted(Comparator.comparing(o -> o.getResource().getName()))
              .collect(Collectors.toList());

          EnvironmentStatusDTO environmentStatus = new EnvironmentStatusDTO(env, statuses);
          environmentStatus.setOverallStatus(getOverallStatus(statuses));
          return environmentStatus;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(statusList);
  }

  @RequestMapping(value = "aggregated/{environmentName}", method = RequestMethod.GET)
  public ResponseEntity<List<AggregatedResourceStatus>> getStatus(
      @PathVariable("environmentName") String environmentName,
      @RequestParam(value = "resources", required = false) Set<String> resources,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {


    List<AggregatedResourceStatus> aggStatusses = aggregatedStatusDAO.getAggregatedStatuses(environmentName, resources, startDate, endDate)
        .stream()
        .sorted(Comparator.comparing(o -> o.getResource().getName()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(aggStatusses);
  }

  private Status getOverallStatus(List<ResourceStatus> statuses) {
    List<Integer> statusesIds = statuses
            .stream()
            .map(r -> r.getStatus().getSeriaNumber())
            .collect(Collectors.toList());
    return statusesIds.stream().allMatch(e -> e == Status.Unknown.getSeriaNumber()) ?
            Status.Unknown :
            Status.fromSerialNumber(statusesIds.stream().filter(e -> e != Status.Unknown.getSeriaNumber()).max(Integer::compare).get());

  }
}

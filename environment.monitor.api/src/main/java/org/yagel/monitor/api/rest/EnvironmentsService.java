package org.yagel.monitor.api.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ScheduleRunnerImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class EnvironmentsService {


  @RequestMapping(value = "environments", method = RequestMethod.GET)
  public List<String> getEnvironments() {

    List<String> envNames = ScheduleRunnerImpl.getInstance()
        .getConfig()
        .getEnvironments()
        .stream()
        .map(EnvironmentConfig::getEnvName)
        .collect(Collectors.toList());

    return envNames;

  }

}

package org.yagel.monitor.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.ScheduleRunner;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.api.rest.dto.EnvironmentConfigDTO;
import org.yagel.monitor.mongo.ResourceDAO;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config/")
public class ConfigService extends AbstractService {

  @Autowired
  private ResourceDAO resourceDAO;

  @Autowired
  private ScheduleRunner runner;

  @RequestMapping(value = "/environments", method = RequestMethod.GET)
  public ResponseEntity<List<EnvironmentConfigDTO>> getEnvironments() {


    List<EnvironmentConfigDTO> envConfigs = runner
        .getConfig()
        .getEnvironments()
        .stream()
        .sorted(Comparator.comparing(EnvironmentConfig::getEnvName))
        .map(config -> new EnvironmentConfigDTO(config.getEnvName(), new TreeSet<>(resourceDAO.find(config.getCheckResources()))))
        .collect(Collectors.toList());

    return ResponseEntity.ok(envConfigs);

  }

}

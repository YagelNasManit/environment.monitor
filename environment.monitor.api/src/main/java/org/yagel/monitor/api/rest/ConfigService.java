package org.yagel.monitor.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.api.rest.dto.EnvironmentConfigDTO;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceDAO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config/")
public class ConfigService extends AbstractService {


  @RequestMapping(value = "/environments", method = RequestMethod.GET)
  public List<EnvironmentConfigDTO> getEnvironments() {

    ResourceDAO resDAO = MongoConnector.getInstance().getResourceDAO();


    List<EnvironmentConfigDTO> envConfigs = ScheduleRunnerImpl.getInstance()
        .getConfig()
        .getEnvironments()
        .stream()
        .map(config -> new EnvironmentConfigDTO(config.getEnvName(), resDAO.find(config.getCheckResources())))
        .collect(Collectors.toList());

    return envConfigs;

  }

}

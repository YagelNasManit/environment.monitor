package org.yagel.monitor.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.api.rest.dto.EnvironmentConfigDTO;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceDAO;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config/")
public class ConfigService extends AbstractService {


    @RequestMapping(value = "/environments", method = RequestMethod.GET)
    public ResponseEntity<List<EnvironmentConfigDTO>> getEnvironments() {

        ResourceDAO resDAO = MongoConnector.getInstance().getResourceDAO();

        List<EnvironmentConfigDTO> envConfigs = ScheduleRunnerImpl.getInstance()
                .getConfig()
                .getEnvironments()
                .stream()
                .sorted(Comparator.comparing(EnvironmentConfig::getEnvName))
                .map(config -> {
                    Set<Resource> resources = resDAO.find(config.getCheckResources())
                            .stream().sorted(Comparator.comparing(Resource::getName))
                            .collect(Collectors.toSet());
                    return new EnvironmentConfigDTO(config.getEnvName(), resources);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(envConfigs);

    }

}

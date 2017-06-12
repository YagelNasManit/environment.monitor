package org.yagel.monitor;

import java.util.Map;
import java.util.Set;

public interface EnvironmentConfig {

  String getEnvName();

  String getHost();

  long getTaskDelay();

  int getAppVersion();

  Set<String> getCheckResources();

  Map<String, String> getAdditionalProperties();

}

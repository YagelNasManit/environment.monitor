package org.yagel.monitor.config;

import org.yagel.monitor.EnvironmentConfig;

import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentConfigImpl implements EnvironmentConfig {

  private String evnName;
  private long taskDelay;
  private String host;
  private int appVersion;
  @XmlElementWrapper(name = "checkedResources")
  @XmlElement(name = "resource")
  private Set<String> checkedResources;
  private Map<String, String> additionalProperties;


  @Override
  public String getEnvName() {
    return this.evnName;
  }

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public long getTaskDelay() {
    return taskDelay;
  }

  @Override
  public int getAppVersion() {
    return appVersion;
  }


  @Override
  public Set<String> getCheckResources() {
    return this.checkedResources;
  }

  @Override
  public Map<String, String> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperties(Map<String, String> additionalProperties) {
    this.additionalProperties = additionalProperties;
  }

  public void setAppVersion(int appVersion) {
    this.appVersion = appVersion;
  }

  public void setTaskDelay(long taskDelay) {
    this.taskDelay = taskDelay;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setEvnName(String evnName) {
    this.evnName = evnName;
  }

  public void setCheckedResources(Set<String> checkedResources) {
    this.checkedResources = checkedResources;
  }
}

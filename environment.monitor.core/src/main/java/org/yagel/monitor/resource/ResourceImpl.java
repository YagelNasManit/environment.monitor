package org.yagel.monitor.resource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.yagel.monitor.Resource;

import java.util.Objects;

public class ResourceImpl implements Resource {

  private String id;
  private String name;


  public ResourceImpl(String id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String getId() {
    return this.id;
  }


  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResourceImpl)) return false;
    ResourceImpl resource = (ResourceImpl) o;
    return Objects.equals(getId(), resource.getId()) &&
        Objects.equals(getName(), resource.getName());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("name", name)
        .toString();
  }

  @Override
  public int compareTo(Resource o) {
    return this.getName().compareTo(o.getName());
  }
}

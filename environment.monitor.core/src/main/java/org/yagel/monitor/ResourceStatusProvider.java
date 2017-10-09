package org.yagel.monitor;


public interface ResourceStatusProvider {

  ResourceStatus reloadStatus();

  String getName();

  Resource getResource();


}
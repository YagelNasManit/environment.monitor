package org.yagel.monitor;


import org.yagel.monitor.resource.Status;

public interface ResourceStatusProvider {

  String getName();

  Status reloadStatus();

  Resource getResource();


}
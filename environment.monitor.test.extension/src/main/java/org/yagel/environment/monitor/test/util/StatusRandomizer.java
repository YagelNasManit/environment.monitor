package org.yagel.environment.monitor.test.util;

import org.yagel.monitor.resource.Status;

import java.util.Random;

public class StatusRandomizer {

  public static Status random() {
    int rnd = new Random().nextInt(Status.values().length);
    return Status.values()[rnd];
  }
}

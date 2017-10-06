package org.yagel.monitor.resource;

import javax.xml.bind.annotation.XmlEnumValue;
import java.util.stream.Stream;

public enum Status {

  @XmlEnumValue("Online")
  Online(0), // s0
  @XmlEnumValue("BorderLine")
  BorderLine(1), // s1
  @XmlEnumValue("Unavailable")
  Unavailable(2), // s2
  @XmlEnumValue("Unknown")
  Unknown(3); // s3

  int seriaNumber;

  Status(int seriaNumber) {
    this.seriaNumber = seriaNumber;
  }

  public static Status fromSerialNumber(int serialNumber) {
    return Stream.of(Status.values())
            .filter(status -> status.getSeriaNumber() == serialNumber)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("illegal resource status"));
  }

  public int getSeriaNumber() {
    return seriaNumber;
  }

}
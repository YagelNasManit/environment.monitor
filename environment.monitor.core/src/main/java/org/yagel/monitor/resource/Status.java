package org.yagel.monitor.resource;

import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlEnumValue;

public enum Status {

  @XmlEnumValue("Online")
  Online(0), // s0
  @XmlEnumValue("BorderLine")
  BorderLine(0),
  @XmlEnumValue("Unavailable")
  Unavailable(1), // s1
  @XmlEnumValue("Unknown")
  Unknown(2); // s2

  int seriaNumber;

  Status(int seriaNumber) {
    this.seriaNumber = seriaNumber;
  }

  public static int seriaNumbers() {
    return 3;
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

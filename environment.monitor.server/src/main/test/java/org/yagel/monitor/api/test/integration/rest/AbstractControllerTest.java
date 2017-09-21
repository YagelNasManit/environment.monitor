package org.yagel.monitor.server.test.integration.rest;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.yagel.monitor.Resource;
import org.yagel.monitor.resource.ResourceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractControllerTest extends AbstractTestNGSpringContextTests{

  protected String mockResoureId = "ControllerUnitTestResourceId";
  protected String mockResoureName = "ControllerUnitTestResourceName";

  protected Resource rndResource() {
    return new ResourceImpl(mockResoureId + UUID.randomUUID(), mockResoureName + UUID.randomUUID());
  }

  protected <T> List<T> generateListN(int copies, Supplier<T> supplier) {
    return Stream.generate(supplier).limit(copies).collect(Collectors.toList());
  }

  protected <T> Set<T> generateSetN(int copies, Supplier<T> supplier) {
    return new HashSet<>(generateListN(copies,supplier));
  }
}

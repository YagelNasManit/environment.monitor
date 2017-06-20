package org.yagel.monitor.runner.test.intergation;

import org.apache.commons.lang3.RandomUtils;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractDAOTest {

  protected String mockResoureId = "Mock_resource";
  protected String mockResoureName = "Mock_resource_Name";


  protected Resource rndResource() {
    return new ResourceImpl(mockResoureId + System.currentTimeMillis(), mockResoureName + System.currentTimeMillis());
  }

  protected ResourceStatus rndResStatus() {
    return this.rndResStatus(rndResource());
  }

  protected ResourceStatus rndResStatus(Resource resource) {
    return new ResourceStatusImpl(resource.getId(), rndStatus(), new Date());
  }

  protected ResourceStatus rndResStatus(Resource resource, Status status, Date updated) {
    return new ResourceStatusImpl(resource.getId(), status, updated);
  }


  protected Status rndStatus() {
    return Status.values()[RandomUtils.nextInt(0, Status.values().length)];
  }

  protected Date rndDate(Date start, Date end) {
    long startTime = start.getTime();
    long endTime = end.getTime();

    long diff = endTime - startTime + 1;
    return new Date(startTime + (long) (Math.random() * diff));
  }


  protected <T> List<T> generateN(int copies, Supplier<T> supplier) {
    return Stream.generate(supplier).limit(copies).collect(Collectors.toList());

  }
}

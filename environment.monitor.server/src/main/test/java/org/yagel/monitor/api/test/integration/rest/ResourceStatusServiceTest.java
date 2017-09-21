package org.yagel.monitor.api.test.integration.rest;


import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.yagel.monitor.api.rest.ResourceStatusService;

@WebMvcTest(controllers = ResourceStatusService.class)
public class ResourceStatusServiceTest {
}

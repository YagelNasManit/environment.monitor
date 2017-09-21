package org.yagel.monitor.api.test.integration.rest.unit;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.api.rest.ConfigService;
import org.yagel.monitor.mongo.ResourceDAO;


@WebMvcTest(controllers = ConfigService.class)
public class ConfigServiceTest extends AbstractControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @MockBean
  private ResourceDAO resourceDAO;


  @MockBean
  private ScheduleRunnerImpl scheduleRunner;


  @BeforeMethod
  public void configureMocks() {
    given(resourceDAO.find(Mockito.anySet())).willReturn(resourceList);
    given(scheduleRunner.getConfig()).willReturn(monitorConfig);
  }

  @Test
  public void testCorrectConfigReturned() throws Exception {


    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/config/environments").accept(
        MediaType.APPLICATION_JSON);


    this.mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(envNames.size())))
        .andExpect(jsonPath("$..environmentName", everyItem(isIn(envNames))))
        .andExpect(jsonPath("$[0].checkedResources..id", hasSize(resIds.size())))
        .andExpect(jsonPath("$[0].checkedResources..name", hasSize(resIds.size())))
        .andExpect(jsonPath("$..checkedResources..id", everyItem(isIn(resIds))))
        .andExpect(jsonPath("$..checkedResources..name", everyItem(isIn(resNames))))
        .andReturn();

  }


}

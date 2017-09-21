package org.yagel.monitor.api.test.integration.rest;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.isA;
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
import org.yagel.monitor.api.rest.EnvironmentStatusService;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;

import java.util.ArrayList;

@WebMvcTest(controllers = EnvironmentStatusService.class)
public class EnvironmentStatusServiceTest extends AbstractControllerTest {

  @MockBean
  private ResourceLastStatusDAO lastStatusDAO;

  @MockBean
  private ScheduleRunnerImpl scheduleRunner;

  @Autowired
  private MockMvc mockMvc;


  @BeforeMethod
  public void configureMocks() {
    given(lastStatusDAO.find(Mockito.anySet())).willReturn(statusList);
    given(lastStatusDAO.find(Mockito.anyString())).willReturn(statusList);
    given(lastStatusDAO.find("INVALID")).willReturn(new ArrayList<>());
    given(scheduleRunner.getConfig()).willReturn(monitorConfig);
  }

  @Test
  public void testGetCurrentOverallStatus() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/environment/status/current").accept(
        MediaType.APPLICATION_JSON);


    int totalElemsSize =statusList.size()*envNames.size();

    this.mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name",everyItem(isIn(envNames))))
        .andExpect(jsonPath("$[*].resourcesStatus",not(nullValue())))
        .andExpect(jsonPath("$[*].resourcesStatus",not(emptyArray())))

        // check resource ids
        .andExpect(jsonPath("$[*].resourcesStatus..id",everyItem(isIn(resIds))))
        .andExpect(jsonPath("$[*].resourcesStatus..id",hasSize(totalElemsSize)))

        // check resource names
        .andExpect(jsonPath("$[*].resourcesStatus..name",everyItem(isIn(resNames))))
        .andExpect(jsonPath("$[*].resourcesStatus..name",hasSize(totalElemsSize)))

        // check resource states
        .andExpect(jsonPath("$[*].resourcesStatus..status",everyItem(is("Online"))))
        .andExpect(jsonPath("$[*].resourcesStatus..status",hasSize(totalElemsSize)))

        .andReturn();

  }

  @Test
  public void testGetCurrentStatusForEnvironment() throws Exception {

    String env = envNames.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("empty env names list"));

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/environment/status/current/"+env).accept(
        MediaType.APPLICATION_JSON);


    this.mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name",is(env)))
        .andExpect(jsonPath("$.resourcesStatus",hasSize(statusList.size())))
        .andExpect(jsonPath("$.resourcesStatus..id",everyItem(isIn(resIds))))
        .andExpect(jsonPath("$.resourcesStatus..name",everyItem(isIn(resNames))))
        .andExpect(jsonPath("$.resourcesStatus..status",everyItem(is("Online"))))
        .andReturn();

  }


  @Test
  public void testGetCurrentStatusForNonExistentEnvironment() throws Exception {

    String env = "INVALID";

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/environment/status/current/"+env).accept(
        MediaType.APPLICATION_JSON);


    this.mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name",is(env)))
        .andExpect(jsonPath("$.resourcesStatus",not(emptyArray())))
        .andReturn();

  }
}

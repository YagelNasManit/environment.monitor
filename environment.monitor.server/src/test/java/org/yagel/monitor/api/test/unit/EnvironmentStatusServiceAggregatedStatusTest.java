package org.yagel.monitor.api.test.unit;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yagel.monitor.api.rest.EnvironmentStatusService;
import org.yagel.monitor.mongo.AggregatedStatusDAO;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.AggregatedStatus;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebMvcTest(controllers = EnvironmentStatusService.class)
public class EnvironmentStatusServiceAggregatedStatusTest extends AbstractControllerTest {

  @MockBean
  private AggregatedStatusDAO aggregatedStatusDAO;

  private List<AggregatedResourceStatus> aggResourceStatusList;
  private List<AggregatedStatus> aggStatusList;
  private List<String> statusNames;
  private int statusCount = 100;


  private String defaultStartDate = "2017-09-12T01:00:00.000Z";
  private String defaultEndDate = "2017-09-15T01:00:00.000Z";

  @BeforeMethod
  public void configureMocks() {

    this.aggStatusList = Arrays.stream(Status.values()).map(
        status -> {
          AggregatedStatus st = new AggregatedStatus();
          st.setStatus(Status.Online);
          st.setCount(statusCount);
          return st;
        }
    ).collect(Collectors.toList());

    this.statusNames = Arrays.stream(Status.values()).map(Status::name).collect(Collectors.toList());

    this.aggResourceStatusList = resourceList.stream().map(res -> {
      AggregatedResourceStatus aggregatedResourceStatus = new AggregatedResourceStatus();
      aggregatedResourceStatus.setResource(res);
      aggregatedResourceStatus.setResourceStatuses(aggStatusList);
      aggregatedResourceStatus.setCount(300);
      return aggregatedResourceStatus;
    }).collect(Collectors.toList());


    given(
        aggregatedStatusDAO.getAggregatedStatuses(
            Mockito.anyString(),
            Mockito.isNull(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)))
        .willReturn(aggResourceStatusList);

    given(
        aggregatedStatusDAO.getAggregatedStatuses(
            Mockito.anyString(),
            Mockito.anySet(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)))
        .willReturn(Arrays.asList(aggResourceStatusList.get(0)));

    given(
        aggregatedStatusDAO.getAggregatedStatuses(
            Mockito.eq("INVALID"),
            Mockito.any(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)))
        .willReturn(new ArrayList<>());
  }

  @Test
  public void testGetAggregatedStatus() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/Env")
        .accept(MediaType.APPLICATION_JSON)
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(resourceList.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$..resource.id", everyItem(isIn(resIds))))
        .andExpect(MockMvcResultMatchers.jsonPath("$..resource.name", everyItem(isIn(resNames))))
        .andExpect(jsonPath("$..resourceStatuses", everyItem(hasSize(aggStatusList.size()))))
        .andExpect(jsonPath("$..resourceStatuses..status", everyItem(isIn(statusNames))))
        .andExpect(jsonPath("$..resourceStatuses..count", everyItem(is(statusCount))))
        .andReturn();

  }

  @Test
  public void testGetAggregatedStatusForSingleResource() throws Exception {

    AggregatedResourceStatus status = aggResourceStatusList.get(0);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/Env")
        .accept(MediaType.APPLICATION_JSON)
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
        .param("resources", status.getResource().getId());

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(1)))
        .andExpect(jsonPath("$[0].resource.id", is(status.getResource().getId())))
        .andExpect(jsonPath("$[0].resource.name", is(status.getResource().getName())))
        .andExpect(jsonPath("$[0].resourceStatuses", hasSize(aggStatusList.size())))
        .andExpect(jsonPath("$..resourceStatuses", everyItem(hasSize(status.getResourceStatuses().size()))))
        .andExpect(jsonPath("$..resourceStatuses..status", everyItem(isIn(statusNames))))
        .andExpect(jsonPath("$..resourceStatuses..count", everyItem(is(statusCount))))
        .andReturn();

  }

  @Test
  public void testGetAggregatedStatusForInvalidEnvironment() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/INVALID")
        .accept(MediaType.APPLICATION_JSON)
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(0)))
        .andReturn();

  }

  @Test
  public void testGetAggregatedStatusForMissingStartDate() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/Env")
        .accept(MediaType.APPLICATION_JSON)
        .param("endDate", defaultEndDate);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();

  }

  @Test
  public void testGetAggregatedStatusForMissingEndDate() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/Env")
        .accept(MediaType.APPLICATION_JSON)
        .param("startDate", defaultStartDate);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();

  }

  @Test
  public void testGetAggregatedStatusForNullResources() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/environment/status/aggregated/Env")
        .accept(MediaType.APPLICATION_JSON)
        .param("resources", "")
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate);

    // TODO here bad request should be thrown
    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

  }


}

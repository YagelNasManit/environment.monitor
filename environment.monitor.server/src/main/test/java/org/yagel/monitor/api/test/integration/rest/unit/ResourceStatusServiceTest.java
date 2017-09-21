package org.yagel.monitor.api.test.integration.rest.unit;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.StatusUpdate;
import org.yagel.monitor.api.rest.ResourceStatusService;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.resource.StatusUpdateImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebMvcTest(controllers = ResourceStatusService.class)
public class ResourceStatusServiceTest extends AbstractControllerTest {

  @MockBean
  private ResourceStatusDetailDAO statusDetailDAO;

  @MockBean
  private ScheduleRunnerImpl scheduleRunner;


  @Autowired
  private MockMvc mockMvc;

  private List<StatusUpdate> updateList;
  private int updatesCount;
  private Date date;
  private Status status;

  @BeforeClass
  public void configureMocks() {

    date = new Date();
    status = Status.Online;
    updatesCount = 100;

    updateList = generateListN(updatesCount, () -> new StatusUpdateImpl(status, date));


    given(
        statusDetailDAO.getStatusUpdates(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(updateList);

    given(
        statusDetailDAO.getStatusUpdates(
            Mockito.eq("INVALID"),
            Mockito.anyString(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(new ArrayList<>());

    given(
        statusDetailDAO.getStatusUpdates(
            Mockito.anyString(),
            Mockito.eq("INVALID"),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(new ArrayList<>());
  }

  @Test
  public void testGetResourceStatusWithNoEnvironment() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status//Resource")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  public void testGetResourceStatusWithNoResource() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andReturn();

  }

  @Test
  public void testGetResourceStatusWithNoStartDate() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/Resource")
       /* .param("startDate", "2017-09-12T01:00:00.000Z")*/
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }


  @Test
  public void testGetResourceStatusWithNoEndDate() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/Resource")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void testGetResourceStatusInvalidEnvironment() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/INVALID/Resource")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(0)))
        .andReturn();
  }

  @Test
  public void testGetResourceStatusInvalidResource() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/INVALID")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(0)))
        .andReturn();
  }

  @Test
  public void testGetResourceStatus() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/Resource")
        .param("startDate", "2017-09-12T01:00:00.000Z")
        .param("endDate", "2017-09-15T01:00:00.000Z")
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(updateList.size())))
        .andExpect(jsonPath("$..status", everyItem(is(status.name()))))
        .andExpect(jsonPath("$..updated", everyItem(equalTo(date.getTime()))))
        .andReturn();
  }

}

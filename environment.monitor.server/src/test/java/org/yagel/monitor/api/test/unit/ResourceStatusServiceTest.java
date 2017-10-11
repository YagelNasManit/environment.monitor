package org.yagel.monitor.api.test.unit;


import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.mockito.ArgumentMatchers.isNull;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.StatusUpdate;
import org.yagel.monitor.api.rest.ResourceStatusService;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.resource.StatusUpdateImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebMvcTest(controllers = ResourceStatusService.class)
public class ResourceStatusServiceTest extends AbstractControllerTest {

  @MockBean
  private ResourceStatusDetailDAO statusDetailDAO;

  private List<StatusUpdate> updateList;
  private List<StatusUpdate> updateListDetailed;
  private int updatesCount = 100;
  private Date date = new Date();
  private Status status = Status.Online;
  private String statusDetails = "Details";

  private String defaultStartDate = "2017-09-12T01:00:00.000Z";
  private String defaultEndDate = "2017-09-15T01:00:00.000Z";



  @BeforeClass
  public void configureMocks() {

    updateList = generateListN(updatesCount, () -> new StatusUpdateImpl(status, date));
    updateListDetailed = generateListN(updatesCount, () -> new StatusUpdateImpl(status, date,statusDetails));

    given(
        statusDetailDAO.getStatusUpdatesShort(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(updateList);

    given(
        statusDetailDAO.getStatusUpdatesDetailed(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(updateListDetailed);

    given(
        statusDetailDAO.getStatusUpdatesShort(
            Mockito.eq("INVALID"),
            Mockito.anyString(),
            Mockito.any(Date.class),
            Mockito.any(Date.class)
        )
    ).willReturn(new ArrayList<>());

    given(
        statusDetailDAO.getStatusUpdatesShort(
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
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
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
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
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
        .param("endDate", defaultEndDate)
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
        .param("startDate", defaultStartDate)
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
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
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
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
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
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(updateList.size())))
        .andExpect(jsonPath("$..status", everyItem(is(status.name()))))
        .andExpect(jsonPath("$..updated", everyItem(equalTo(date.getTime()))))
        .andExpect(jsonPath("$..statusDetails", emptyIterable()))
        .andReturn();
  }


  @Test
  public void testGetResourceStatusWithDetailsSetToFalse() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/Resource")
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
        .param("endDate", defaultEndDate)
        .param("statusDetails", "false")

        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(updateList.size())))
        .andExpect(jsonPath("$..status", everyItem(is(status.name()))))
        .andExpect(jsonPath("$..updated", everyItem(equalTo(date.getTime()))))
        .andExpect(jsonPath("$..statusDetails", emptyIterable()))
        .andReturn();
  }

  @Test
  public void testGetResourceStatusWithDetailsSetToTrue() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/resource/status/Env/Resource")
        .param("startDate", defaultStartDate)
        .param("endDate", defaultEndDate)
        .param("endDate", defaultEndDate)
        .param("statusDetails", "true")

        .accept(MediaType.APPLICATION_JSON);

    this.mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", hasSize(updateList.size())))
        .andExpect(jsonPath("$..status", everyItem(is(status.name()))))
        .andExpect(jsonPath("$..updated", everyItem(equalTo(date.getTime()))))
        .andExpect(jsonPath("$..statusDetails", hasSize(updateList.size())))
        .andExpect(jsonPath("$..statusDetails", everyItem(is(statusDetails))))
        .andReturn();
  }
}

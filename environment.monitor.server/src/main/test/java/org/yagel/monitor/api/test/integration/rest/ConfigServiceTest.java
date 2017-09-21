package org.yagel.monitor.server.test.integration.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.api.Application;
import org.yagel.monitor.api.rest.ConfigService;
import org.yagel.monitor.mongo.ResourceDAO;

import static org.mockito.BDDMockito.given;

import java.util.Set;


@WebMvcTest(controllers = {Application.class,ConfigService.class })
@SpringBootTest(classes = Application.class)
public class ConfigServiceTest extends AbstractControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ResourceDAO resourceDAO;


  @MockBean
  private ScheduleRunnerImpl scheduleRunner;


  /*@InjectMocks
  private ConfigService configService;*/



 /* @BeforeMethod
  public void initTests() {
    MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }*/

  @Test
  public void testControllerIs() {
    //Assert.assertNotNull();
  }


  @Test
  public void testCorrectConfigReturned() throws Exception {

    Set<Resource> resourceList = generateSetN(10, this::rndResource);


    given(resourceDAO.find(Mockito.anySet())).willReturn(resourceList);


    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/config/environments").accept(
        MediaType.APPLICATION_JSON);


    mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));


    //System.out.println(result.getResponse());

    /*JSONAssert.assertEquals(expected, result.getResponse()
        .getContentAsString(), false);*/
  }

}

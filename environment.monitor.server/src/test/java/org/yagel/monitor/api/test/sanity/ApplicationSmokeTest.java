package org.yagel.monitor.api.test.sanity;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationSmokeTest extends AbstractTestNGSpringContextTests {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void greetingShouldReturnDefaultMessage() throws Exception {
    ResponseEntity<String> homePage = this.restTemplate.getForEntity("http://localhost:" + port + "/",
        String.class);


    MatcherAssert.assertThat(homePage.getStatusCodeValue(), Matchers.equalTo(200));
    MatcherAssert.assertThat(homePage.getStatusCode(), Matchers.equalTo(HttpStatus.OK));
  }

}

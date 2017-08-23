package org.yagel.monitor.api.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {

  @RequestMapping(value = "/{[path:[^\\.]*}")
  public String redirect() {
    return "forward:/";
  }

}

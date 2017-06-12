package org.yagel.monitor.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.ui.view.MainView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;


@Theme("monitortheme")
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
public class MonitorUI extends UI {

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    setContent(init());
  }

  private Layout init() {
    //check changes every 30 seconds
    setPollInterval(30_000);
    return new MainView();

  }


  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = MonitorUI.class, productionMode = false)
  public static class VaadinMonitorServlet extends VaadinServlet {

    @Override
    public void init() throws ServletException {
      super.init();

      // start  schedule runner
      ScheduleRunnerImpl.newInstance(getServletContext().getClassLoader()).runTasks();
    }

    @Override
    public void destroy() {
      super.destroy();

      // kill schedule runner & mongo connection
      ScheduleRunnerImpl.getInstance().shutdown();
      MongoConnector.getInstance().close();
    }
  }

}

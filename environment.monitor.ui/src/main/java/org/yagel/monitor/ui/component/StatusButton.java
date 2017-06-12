package org.yagel.monitor.ui.component;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.ui.MonitorUI;

import java.io.File;
import java.net.URISyntaxException;

public class StatusButton extends Button {

  private static FileResource greenIcon = null;
  private static FileResource redIcon = null;
  private static FileResource yellowIcon = null;

  static {
    try {
      greenIcon = new FileResource(new File(MonitorUI.class.getResource("/store_green_icon.png").toURI()));
      redIcon = new FileResource(new File(MonitorUI.class.getResource("/store_red_icon.png").toURI()));
      yellowIcon = new FileResource(new File(MonitorUI.class.getResource("/store_yellow_icon.png").toURI()));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private Resource resource;


  /**
   * The constructor initialize actual resource's status, name and description
   *
   * @param resource resource which status need to be updated
   */
  public StatusButton(Resource resource) {
    super(resource.getName());
    this.resource = resource;
    setDescription(this.resource.getName());

  }

  public Resource getResource() {
    return resource;
  }

  /**
   * Change resource's icon
   *
   * @param resource resource which status need to be updated
   */
  public void update(ResourceStatus resource) {
    setStateIcon(resource.getStatus());
  }

  /**
   * Sets icon image depends on status
   *
   * @param status status which need to be set
   */
  private void setStateIcon(Status status) {
    switch (status) {
      case Online:
        this.setIcon(greenIcon);
        break;
      case Unavailable:
        this.setIcon(redIcon);
        break;
      case Unknown:
        this.setIcon(yellowIcon);
        break;
    }
  }


}

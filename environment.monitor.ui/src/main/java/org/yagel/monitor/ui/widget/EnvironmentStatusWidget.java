package org.yagel.monitor.ui.widget;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.UpdateStatusListener;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.ui.common.AbstractMultipleResourcesWidget;
import org.yagel.monitor.ui.component.StatusButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnvironmentStatusWidget extends AbstractMultipleResourcesWidget implements UpdateStatusListener {

  private static String WIDGET_TITLE = "Environment: <strong>%s</strong>";
  private static String UPDATED_DATE = "Updated:<br>%s";

  private Map<String, StatusButton> controls;
  private Label titleLabel;
  private List<Button.ClickListener> clickListeners;
  private VerticalLayout widgetLayout;

  public EnvironmentStatusWidget(String environmentName, Set<String> resources, List<Button.ClickListener> listeners) {
    super(environmentName, resources);
    this.controls = new HashMap<>();
    this.addStyleName("current-status-widget");
    this.setSizeUndefined();
  }

  @Override
  public void loadWidget() {

    this.widgetLayout = new VerticalLayout();

    Set<Resource> resources = loadResources();
    initWidgetHeader();
    initWidgetBody(resources);

    List<ResourceStatus> lastStates = loadLastResourceStates();
    reloadWidgetData(lastStates);

    this.setContent(widgetLayout);

  }


  private void initWidgetHeader() {

    this.setCaptionAsHtml(true);
    this.setCaption(String.format(WIDGET_TITLE, environmentName, new Date()));

    this.titleLabel = new Label();
    this.titleLabel.setContentMode(ContentMode.HTML);
    this.widgetLayout.addComponent(titleLabel);


    refreshUpdatedDate();

  }


  private void initWidgetBody(Set<Resource> data) {

    for (Resource resource : data) {
      StatusButton statusButton = new StatusButton(resource);
      controls.put(resource.getId(), statusButton);
      widgetLayout.addComponent(statusButton);
      initClickListeners(statusButton, clickListeners);
    }
  }


  private void reloadWidgetData(List<ResourceStatus> lastStates) {
    for (ResourceStatus resource : lastStates) {
      controls.get(resource.getResourceId()).update(resource);
    }
  }

  private void refreshUpdatedDate() {
    titleLabel.setValue(String.format(UPDATED_DATE, new Date()));
  }

  private void initClickListeners(StatusButton button, List<Button.ClickListener> clickListeners) {

    if (clickListeners == null || clickListeners.isEmpty())
      return;

    for (Button.ClickListener listener : clickListeners)
      button.addClickListener(listener);
  }

  private List<ResourceStatus> loadLastResourceStates() {
    return MongoConnector.getInstance().getLastStatusDAO().find(environmentName, resourcesToDisplayId);
  }


  private Set<Resource> loadResources() {
    return MongoConnector.getInstance().getResourceDAO().find(resourcesToDisplayId);
  }


  @Override
  public void update(Map<Resource, ResourceStatus> lastChangedStatus) {
    reloadWidgetData(new ArrayList<>(lastChangedStatus.values()));
    refreshUpdatedDate();
  }

  @Override
  public boolean isActive() {
    return true;
  }
}

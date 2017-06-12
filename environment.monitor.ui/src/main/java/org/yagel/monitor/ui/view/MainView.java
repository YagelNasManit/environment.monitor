package org.yagel.monitor.ui.view;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

public class MainView extends HorizontalLayout {

  private final Sidebar sidebar;
  private final ViewContent viewContent;

  public MainView() {
    setSizeFull();
    addStyleName("mainview");
    setSpacing(false);


    sidebar = new Sidebar();
    viewContent = new ViewContent();

    addComponent(sidebar);
    addComponent(viewContent);

    setExpandRatio(viewContent, 1);

    initNavigator();
  }

  private void initNavigator() {
    Navigator navigator = new Navigator(UI.getCurrent(), viewContent);
    navigator.addView("", CurrentEnvironmentsStatusView.class);

    for (MonitorView monitorView : MonitorView.values())
      navigator.addView(monitorView.getViewName(), monitorView.getViewClass());

  }


}

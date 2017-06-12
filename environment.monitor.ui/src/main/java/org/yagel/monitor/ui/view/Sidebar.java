package org.yagel.monitor.ui.view;

import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.yagel.monitor.ui.MonitorUI;

import java.io.File;
import java.net.URISyntaxException;

public class Sidebar extends CustomComponent {


  FileResource monitorHeader = null;

  {
    try {
      monitorHeader = new FileResource(new File(MonitorUI.class.getResource("/monitor_header.png").toURI()));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

  public Sidebar() {
    setCompositionRoot(buildContent());
    setPrimaryStyleName("valo-menu");
    setSizeUndefined();
  }


  private Component buildContent() {
    final CssLayout menuContent = new CssLayout();
    menuContent.addStyleName("sidebar");
    menuContent.addStyleName(ValoTheme.MENU_PART);
    menuContent.addStyleName("no-vertical-drag-hints");
    menuContent.addStyleName("no-horizontal-drag-hints");
    menuContent.setWidth(null);
    menuContent.setHeight("100%");

    menuContent.addComponent(buildTitle());
    menuContent.addComponent(buildMenu());

    return menuContent;
  }

  private Component buildMenu() {

    Layout menuLayout = new VerticalLayout();
    menuLayout.addStyleName("valo-menuitems");

    for (MonitorView monitorView : MonitorView.values()) {
      menuLayout.addComponent(new ValoMenuItemButton(monitorView));
    }


    return menuLayout;
  }

  private Component buildTitle() {

    Label logo = new Label("Environment <strong>Monitor</strong>",
        ContentMode.HTML);
    logo.setSizeUndefined();
    Image monitorImage = new Image("", monitorHeader);
    monitorImage.setWidth("100px");
    VerticalLayout logoWrapper = new VerticalLayout();
    logoWrapper.addComponent(monitorImage);
    logoWrapper.addComponent(logo);
    logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
    logoWrapper.addStyleName("valo-menu-title");
    logoWrapper.setSpacing(false);
    return logoWrapper;
  }

  public final class ValoMenuItemButton extends Button {

    private static final String STYLE_SELECTED = "selected";

    private final MonitorView view;

    public ValoMenuItemButton(final MonitorView view) {
      this.view = view;
      setPrimaryStyleName("valo-menu-item");
      //setIcon(view.getIcon());
      setCaption(view.name());
      addClickListener((ClickListener) event -> UI.getCurrent().getNavigator()
          .navigateTo(view.getViewName()));

    }

  }
}

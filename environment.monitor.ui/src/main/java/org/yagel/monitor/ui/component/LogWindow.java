package org.yagel.monitor.ui.component;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.yagel.monitor.ResourceStatus;

import java.util.List;
import java.util.stream.Collectors;

public class LogWindow extends Window {

  VerticalLayout layout;


  public LogWindow(List<ResourceStatus> statusList) {
    super("Log Viewer");

    configureWindow();
    initContent(statusList);

    setContent(layout);

  }


  private void configureWindow() {
    center();
    this.setWidth("40%");
    setClosable(true);
  }

  private void initContent(List<ResourceStatus> statusList) {
    layout = new VerticalLayout();

    TextArea textArea = new TextArea();
    textArea.setRows(30);
    textArea.setWordWrap(true);
    textArea.setWidth("100%");
    String str = statusList.stream().map(this::composeResourceStatusMessage).collect(Collectors.joining());
    textArea.setValue(str);


    layout.addComponent(textArea);
  }

  private String composeResourceStatusMessage(ResourceStatus resStat) {
    return "" + resStat.getResourceId() + " " + resStat.getStatus() + " " + resStat.getUpdated() + "\n\n";
  }


}

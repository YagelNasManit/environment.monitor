package org.yagel.monitor.ui.widget;

import com.vaadin.data.HasValue;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import org.yagel.monitor.ui.common.AbstractMultipleResourcesWidget;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectViewRangeWidget extends AbstractMultipleResourcesWidget {

  private String defaultResource;
  private LocalDateTime defaultDate;
  private HorizontalLayout widgetLayout;
  private Set<SelectionChangedListener> listeners;

  public SelectViewRangeWidget(String environmentName, Set<String> resourcesToDisplayId, LocalDateTime defaultDate,
      String defaultResource) {
    super(environmentName, resourcesToDisplayId);
    this.defaultResource = defaultResource;
    this.defaultDate = defaultDate;
  }


  @Override
  public void loadWidget() {
    this.widgetLayout = new HorizontalLayout();
    widgetLayout.setSpacing(true);
    widgetLayout.setMargin(true);


    DateField startDate = new DateField("Select Day to display", defaultDate.toLocalDate());
    ComboBox<String> comboBox = new ComboBox<>("Select resource to display");
    comboBox.setItems(resourcesToDisplayId);
    comboBox.setValue(defaultResource);

    widgetLayout.addComponent(startDate);
    widgetLayout.addComponent(comboBox);

    this.setCaption("Pls choose resource and date to display");
    this.setContent(widgetLayout);


    HasValue.ValueChangeListener changeListener =
        event ->
            listeners.forEach(
                viewChangeListener ->
                    viewChangeListener.selectionChanged(startDate.getValue().atTime(LocalTime.now()), comboBox.getValue()
                    )
            );


    startDate.addValueChangeListener(changeListener);
    comboBox.addValueChangeListener(changeListener);


  }

  public void setSelectionChangedListeners(SelectionChangedListener... listeners) {
    this.listeners = Stream.of(listeners).collect(Collectors.toSet());
  }

  public interface SelectionChangedListener {

    void selectionChanged(LocalDateTime date, String resourceId);
  }


}

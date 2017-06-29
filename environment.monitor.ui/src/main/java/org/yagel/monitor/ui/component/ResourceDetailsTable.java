package org.yagel.monitor.ui.component;

import com.vaadin.ui.Grid;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceDetailsTable {

  private final Map<String, List<ResourceStatus>> statusListGroupped;

  public ResourceDetailsTable(Map<String, List<ResourceStatus>> statusListGroupped) {
    this.statusListGroupped = statusListGroupped;
  }

  public Grid loadTable() {


    List<DTO> dtoList = statusListGroupped.entrySet().stream().map(
        entry -> {
          DTO dto = new DTO(entry.getKey());
          dto.availableCount = calcStatsuCount(entry.getValue(), Status.Online);
          dto.unavailableCount = calcStatsuCount(entry.getValue(), Status.Unavailable);
          dto.unknownCount = calcStatsuCount(entry.getValue(), Status.Unknown);
          dto.borderLineCount = calcStatsuCount(entry.getValue(), Status.BorderLine);
          return dto;
        }
    ).collect(Collectors.toList());


    Grid<DTO> grid = new Grid<>();
    grid.setItems(dtoList);
    grid.addColumn(DTO::getHour).setCaption("Hour");
    grid.addColumn(DTO::getAvailableCount).setCaption("Available");
    grid.addColumn(DTO::getUnavailableCount).setCaption("Unavailable");
    grid.addColumn(DTO::getUnknownCount).setCaption("Unknown");
    grid.addColumn(DTO::getBorderLineCount).setCaption("Border Line");
    grid.appendFooterRow();
    grid.appendHeaderRow();

    return grid;

  }

  private long calcStatsuCount(List<ResourceStatus> statusList, Status status) {
    return statusList
        .stream()
        .filter(resourceStatus -> resourceStatus.getStatus() == status)
        .count();
  }

  private class DTO {
    String hour;
    long availableCount;
    long unavailableCount;
    long unknownCount;
    long borderLineCount;

    public DTO(String hour) {
      this.hour = hour;
    }

    public String getHour() {
      return hour;
    }

    public long getAvailableCount() {
      return availableCount;
    }

    public long getUnavailableCount() {
      return unavailableCount;
    }

    public long getUnknownCount() {
      return unknownCount;
    }

    public long getBorderLineCount() {
      return borderLineCount;
    }
  }
}

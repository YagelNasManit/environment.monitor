package org.yagel.monitor.ui.component;

import com.vaadin.ui.Grid;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.List;

public class ResourceDetailsTable {


  public Grid loadTable() {

    List<DTO> dtoList = new ArrayList<>();
    dtoList.add(new DTO(Status.Online, 100));
    dtoList.add(new DTO(Status.Unavailable, 100));
    dtoList.add(new DTO(Status.Unknown, 100));
    dtoList.add(new DTO(Status.BorderLine, 100));


    Grid<DTO> grid = new Grid<>();
    grid.setItems(dtoList);
    grid.addColumn(DTO::getStatus).setCaption("Status");
    grid.addColumn(DTO::getCount).setCaption("Status count");

    return grid;

  }

  private class DTO {
    Status status;
    int count;

    public DTO(Status status, int count) {
      this.status = status;
      this.count = count;
    }

    public Status getStatus() {
      return status;
    }

    public int getCount() {
      return count;
    }
  }

}

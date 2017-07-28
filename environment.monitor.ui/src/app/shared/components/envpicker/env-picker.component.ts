import {Component, EventEmitter, Output} from "@angular/core";


@Component({
  selector: 'env-picker',
  templateUrl: 'env-picker.component.html',

})
export class EnvPickerComponent {

  @Output() onEnvSelected = new EventEmitter<string>();

  onChange(value) {
    console.log(`env selection changed: ${value}`)
    this.onEnvSelected.emit(value);
  }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from "../../../service/user.service";
import {ToastsManager} from "ng2-toastr";
import {HelperService} from "../../../service/helper.service";
import {BlockUI, NgBlockUI} from "ng-block-ui";
import {waitString} from "../../../app.module";

declare var $: any;

@Component({
  selector: 'user-add-modal',
  templateUrl: './user-add-modal.component.html',
})
export class UserAddModalComponent implements OnInit {
  @Output() userChangedOrAdded = new EventEmitter<boolean>();
  @Input() user = {id: null, firstname: null, middlename: null, lastname: null, gender: null, login: null, password: null, roles: []};
  confirmPassword = null;
  @Input() selectedRoles = [];
  availableRoles = [];
  @Input() isAddingNewUser = true;
  roleSelectDropdownSettings = {
    singleSelection: false,
    enableCheckAll: false,
    classes: "selectRole",
    text: "Выберите роль"
  };
  @BlockUI() blockUI: NgBlockUI;

  constructor(private toast: ToastsManager, private userService: UserService) {}

  ngOnInit() {
    this.blockUI.start(waitString);
    this.userService.getAvailableRoles().subscribe(
      (data: string[]) => {
        data.forEach(role => {
          this.availableRoles.push({
            id: this.availableRoles.length + 1,
            original: role,
            itemName: HelperService.convertOriginalToRole(role)
          });
        });
        this.blockUI.stop();
      },
      error => {
        this.blockUI.stop();
        if (error.error.message != undefined) this.toast.error(error.error.message, "Ошибка");
        else this.toast.error(error.error, "Ошибка")
      }
    );
  }

  add() {
    if (this.user.password !== this.confirmPassword) {
      this.toast.error("Пароли не совпадают", "Ошибка");
      return;
    }
    let roles = [];
    this.selectedRoles.forEach(object => roles.push(object.original));
    this.user.roles = roles;
    this.user.gender = HelperService.convertRussianToGender(this.user.gender);
    this.blockUI.start(waitString);
    this.userService.addUser(this.user).subscribe(
      data => {
        this.blockUI.stop();
        this.toast.success("Пользователь был добавлен", "Успешно");
        this.userChangedOrAdded.emit(true);
        $('#userAddModal').modal('hide');
        this.confirmPassword = null;
      },
      error => {
        this.blockUI.stop();
        if (error.error.message != undefined) this.toast.error(error.error.message, "Ошибка");
        else this.toast.error(error.error, "Ошибка");
      }
    )
  }

  clearWindow() {
    this.user = {id: null, firstname: null, middlename: null, lastname: null, gender: null, login: null, password: null, roles: []};
    this.confirmPassword = null;
    this.selectedRoles = [];
  }

  update() {
    if ((this.user.password != null || this.confirmPassword != null) && this.user.password !== this.confirmPassword) {
      this.toast.error("Пароли не совпадают", "Ошибка");
      return;
    }
    let roles = [];
    this.selectedRoles.forEach(object => roles.push(object.original));
    this.user.roles = roles;
    this.user.gender = HelperService.convertRussianToGender(this.user.gender);

    this.blockUI.start(waitString);
    this.userService.updateUser(this.user).subscribe(
      data => {
        this.toast.success("Данные обновлены", "Успешно");
        this.userChangedOrAdded.emit(true);
        this.confirmPassword = null;
        $('#userAddModal').modal('hide');
        this.blockUI.stop()
      },
      error => {
        this.blockUI.stop();
        if (error.error.message != undefined) this.toast.error(error.error.message, "Ошибка");
        else this.toast.error(error.error, "Ошибка");
      }
    )
  }
}

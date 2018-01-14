import {Component, OnInit} from '@angular/core';
import {ToastsManager} from 'ng2-toastr/ng2-toastr';
import {AuthService} from "../../security/auth.service";
import {currentPrincipal} from "../../security/auth.service";
import {HelperService} from "../../service/helper.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {

  principal = currentPrincipal;
  rolesAndLinks = [];

  constructor(private toast: ToastsManager, private authService: AuthService) {
    this.authService.principalReady.subscribe(item => this.generateMenuLink())
  };

  logout() {
    this.authService.logout().subscribe(
      data => {
        this.toast.warning("Вы вышли из аккаунта", "Внимание");
        setTimeout(function () {
          location.reload()
        }, 500)
      },
      error => {
        if (error.error.message != undefined) this.toast.error(error.error.message, "Ошибка");
        else this.toast.error(error.error, "Ошибка");
      }
    )
  }

  generateMenuLink() {
    this.principal.roles.forEach(r => {
      this.rolesAndLinks.push(
        {
          role: HelperService.convertOriginalToRole(r),
          link: "/" + r.toLowerCase()
        })
    });
  }
}

import {Component, OnInit, Input, Output, EventEmitter} from "@angular/core";
import {Router, ActivatedRoute} from "@angular/router";

import {SessionService} from "../../services/session.service";

import {BaseSessionComponent} from "../base/base-session.component";
import {MessageObject} from "../../shared/message.object";
import {MessageService} from "../../services/message.service";
import {User} from "../../entity/user.entity";
import {PreferencesService} from "../../services/preferences.service";
import {SimplePageScrollService} from "ng2-simple-page-scroll";

@Component({
  selector: 'app-preferences-favourites',
  templateUrl: 'preferences-favourites.component.html'
})
export class PreferencesFavouritesComponent extends BaseSessionComponent implements OnInit {

  @Output()
  private relogin = new EventEmitter<boolean>();

  @Input() private fUser:User;
  private message:MessageObject;
  private submitButtonDisabled = false;
  private submitButtonHtml = "Guardar";


  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private preferencesService: PreferencesService,
    private simplePageScrollService: SimplePageScrollService,
    sessionService: SessionService
  ) { super(sessionService) }

  ngOnInit() {
    super.ngOnInit();
    console.log("Init PreferencesFavouritesComponent");
  }

  /*
   * Formulario
   */
  private onSubmit(event) {
    event.stopPropagation();
    console.log("Submit");

    this.submitButton(false);
    this.preferencesService.savePreferences(this.fUser).subscribe(
      response => this.submitFormSuccess(response),
      error => this.submitFormError(error)
    );
  }

  private submitFormSuccess(user:User) {
    this.fUser = user;
    this.sessionService.relogin().subscribe(
      respone => this.relogin.emit(true),
      error => console.log(error)
    );

    this.submitButton(true);
    this.message = this.messageService.getMessage(300);
    this.simplePageScrollService.scrollToElement("#message_favourites", 0);
  }

  private submitFormError(error:any) {
    if(error._body != "") {
      error = JSON.parse( error._body );
      if(error.code) {
        this.message = {
          "code": error.code,
          "message": error.message,
          "isError": true
        };
      }
    }
    if(this.message == null)
      this.message = this.messageService.getMessage(301);

    this.submitButton(true);
    this.simplePageScrollService.scrollToElement("#message_favourites", 0);
  }

  // Estado del boton submit
  private submitButton(enabled:boolean) {
    if(enabled) {
      this.submitButtonDisabled = false;
      this.submitButtonHtml = "Guardar";
    } else {
      this.submitButtonDisabled = true;
      this.submitButtonHtml = "Cargando...";
    }
  }

  /*
   * Overwrited
   */
  protected onLoginCalls() {}
  protected onReloginCalls() {}
  protected onLogoutCalls() {
    this.router.navigate(['/']);
  }
}

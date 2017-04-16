import {Component, OnInit, ViewChild} from "@angular/core";
import {Router, ActivatedRoute} from "@angular/router";

import {AsideOptionsComponent} from "../../aside-options/aside-options.component";
import {SessionService} from "../../../services/session.service";

import {BaseSessionComponent} from "../../base/base-session.component";
import {EditorService} from "../../../services/editor.service";

@Component({
  selector: 'app',
  templateUrl: 'form.component.html'
})
export class EditorFormComponent extends BaseSessionComponent implements OnInit {

  // Vistas
  @ViewChild('appAsideOptions') appAsideOptions: AsideOptionsComponent;

  // Variables
  private optionActiveStr = "editor-form";


  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private editorService: EditorService,
    sessionService: SessionService
  ) { super(sessionService) }

  ngOnInit() {
    super.ngOnInit();
    if(!this.hasRole(["ROLE_EDITOR", "ROLE_ADMIN"]))
      this.router.navigate(['/']);

    console.log("Init EditorFormComponent");

    let id = this.activatedRoute.snapshot.params['id'];
  }

  /*
   * Overwrited
   */
  protected onLoginCalls() {}
  protected onLogoutCalls() {
    this.router.navigate(['/']);
  }
}
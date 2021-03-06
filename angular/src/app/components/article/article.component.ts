import {Component, OnInit, ElementRef, ViewChild, DoCheck} from '@angular/core';
import {Router, ActivatedRoute, Params} from "@angular/router";
import {Http} from "@angular/http";
import {SessionService} from "app/services/session.service";

import {Article} from "app/entity/article.entity";
import {URL_IMAGES} from "app/shared/config.object";

import {ArticleService} from "../../services/article.service";
import {BaseSessionComponent} from "../base/base-session.component";
import {MessageObject} from "app/shared/message.object";
import {CommentService} from "../../services/comment.service";
import {MessageService} from "../../services/message.service";
import {SimplePageScrollService} from "ng2-simple-page-scroll";
import {Comment} from "../../entity/comment.entity";

@Component({
  selector: 'app',
  templateUrl: 'article.component.html',
  styleUrls: []
})
export class ArticleComponent extends BaseSessionComponent implements OnInit, DoCheck {

  private articleId:number = -1;
  private article: Article;
  private articleLoading = false;

  private articlesRandom:Article[] = [];
  private urlImages = URL_IMAGES;
  private message: MessageObject;

  // Formulario de comentarios
  @ViewChild('buttonSubmit') buttonSubmit: ElementRef;
  private comment: string;

  // Lista de comentarios
  private pageComments: number;
  private lastPageComments:boolean;
  private comments: Comment[];


  constructor(
    private http: Http,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private articleService: ArticleService,
    private commentService: CommentService,
    private messageService: MessageService,
    private simplePageScrollService: SimplePageScrollService,
    sessionService: SessionService
  ) {
    super(sessionService)
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngDoCheck() {
    let id = this.activatedRoute.snapshot.params['id'];

    if(id > 0 && this.article == null && this.articleId == -1 || id > 0 && id != this.articleId) {
      this.articleId = id;
      this.init();
    }
  }

  private init() {
    this.articleLoading = true;
    this.pageComments = 1;
    this.comments = [];
    this.lastPageComments = false;

    this.sectionArticle();
    this.sectionRandomArticles();
  }

  private sectionArticle() {
    let id = this.activatedRoute.snapshot.params['id'];

    this.articleService.getArticleByID(id).subscribe(
      response => {
        this.article = response;
        this.article.nComments = 0;
        this.articleLoading = false;
        this.comments = [];
        this.sectionComments(this.pageComments);
      },
      error => {
        console.log(error);
        this.router.navigate(['/error/404']);
      }
    );
  }

  private sectionRandomArticles() {
    this.articleService.getArticlesRandom(4).subscribe(
      response => this.articlesRandom = response,
      error => console.log(error)
    );
  }

  private sectionComments(page:number) {
    this.articleService.getCommentsFromArticle( this.article, page ).subscribe(
      response => {
        this.article.nComments = response.totalElements;
        this.comments = this.comments.concat( response.content );
        this.lastPageComments = response.last;
      },
      error => console.log(error)
    );
  }

  /*
   * Eventos
   */

  // Cargar mas comentarios
  private moreComments() {
    this.pageComments++;
    this.sectionComments(this.pageComments);
  }

  /*
   * Formulario
   */

  // Guardar formulario
  private submitForm(event) {
    event.stopPropagation();

    console.log("Submit Form");

    this.buttonSubmitDisable();
    this.articleService.saveComment(this.article, this.comment).subscribe(
      response => this.submitFormSuccess(response),
      error => this.submitFormError(error)
    );
  }

  // Resultado de guardar formulario
  private submitFormSuccess(comment:Comment) {
    // Añadir el comentario en el primer lugar
    let comments:Comment[] = [];
    comments.push(comment);
    comments = comments.concat( this.comments );
    this.comments = comments;
    this.article.nComments++;

    this.buttonSubmitEnable();
    //this.message = this.messageService.getMessage(500);
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
      this.message = this.messageService.getMessage(501);

    this.buttonSubmitEnable();
    this.simplePageScrollService.scrollToElement("#message", 0);
  }

  // Desactivar/Activar boton submit
  private buttonSubmitDisable() {
    this.buttonSubmit.nativeElement.innerHTML = "<i class='fa fa-spinner'></i> Cargando...";
    this.buttonSubmit.nativeElement.disabled = true;
  }

  private buttonSubmitEnable() {
    this.buttonSubmit.nativeElement.innerHTML = "Enviar";
    this.buttonSubmit.nativeElement.disabled = false;
  }

  /*
   * Overwrited
   */
  protected onLoginCalls() {}
  protected onReloginCalls() {}
  protected onLogoutCalls() {}
}

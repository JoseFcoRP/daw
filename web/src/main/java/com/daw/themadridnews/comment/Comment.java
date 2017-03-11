package com.daw.themadridnews.comment;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.daw.themadridnews.article.Article;
import com.daw.themadridnews.user.User;

@Entity
@Table(name="comments")
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	
	@NotNull
	@ManyToOne
	protected Article article;
	
	@NotNull
	@OneToOne
	protected User author;
	
	@NotNull
	protected long number; // Posicion del comentario en un articulo
	
	@Column(nullable = false, length = 500)
	protected String comment;
	
	@NotNull
	protected Date dateInsert;
	
	
	public Comment() {}

	public Comment(Article article, User author, long number, String comment) {
		this.article = article;
		this.author = author;
		this.number = number;
		this.comment = comment;
		this.dateInsert = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	
	public long getNumber() {
		return number;
	}
	
	public void setNumber(long number) {
		this.number = number;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDateInsert() {
		return dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", author=" + author + ", number="+ number +", comment=" + comment + ", dateInsert=" + dateInsert + "]";
	}
}
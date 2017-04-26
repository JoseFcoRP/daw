package com.daw.themadridnews.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.daw.themadridnews.article.Article;
import com.daw.themadridnews.article.ArticleRepository;
import com.daw.themadridnews.article.ArticleView;
import com.daw.themadridnews.comment.CommentRepository;
import com.daw.themadridnews.user.User;
import com.daw.themadridnews.webconfig.Config;
import com.fasterxml.jackson.annotation.JsonView;

@Service
public class SearchService {
	
	public static interface SearchBasic extends Article.Basic, User.Basic, Config.Responses {}
	
	public static final int N_RESULTS = 10;
	
	@Autowired
	protected ArticleRepository articleRepository;
	
	@Autowired
	protected CommentRepository commentRepository;
	

	// Devuelve una pagina de articulos para una categoria determinada
	// Recordar que las paginas empiezan en 0
	public Page<Article> getArticlesContaining(String item, int nPage) {
		Page<Article> p = articleRepository.findByTitleContainingAndVisible(item, true, new PageRequest(nPage, N_RESULTS));
		return p;
	}
	
	
	/*************/
	
	public class PageArticlesView {
		@JsonView(SearchService.SearchBasic.class) public List<ArticleView> content;
		@JsonView(SearchService.SearchBasic.class) public boolean isLast;
	}
}

package com.daw.themadridnews.search;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.daw.themadridnews.article.Article;
import com.daw.themadridnews.article.ArticleService;
import com.daw.themadridnews.article.ArticleView;
import com.daw.themadridnews.article.CategoryCommons;
import com.daw.themadridnews.article.CategoryView;
import com.daw.themadridnews.comment.CommentRepository;
import com.daw.themadridnews.comment.CommentService;
import com.daw.themadridnews.comment.CommentView;
import com.daw.themadridnews.search.SearchService.PageArticlesView;
import com.daw.themadridnews.webconfig.Config;


@Controller
public class SearchController {
	
	@Autowired protected ArticleService articleService;
	@Autowired protected CommentService commentService;
	@Autowired protected CommentRepository commentRepository;
	@Autowired protected SearchService searchService;
	@Autowired protected Config config;
	
	
	@RequestMapping(value="/buscar", method=RequestMethod.GET)
	public ModelAndView categories(Model model, @RequestParam String search){
		if(search == null)
			return new ModelAndView( new RedirectView("/portada") );
		
		// Resultados de la busqueda
		Page<Article> p = searchService.getArticlesContaining(search, 0);
		
		PageArticlesView pav = searchService.new PageArticlesView();
		pav.content = ArticleView.castList( p.getContent(), commentRepository );
		pav.isLast = p.isLast();
		
		model.addAttribute("search", search);
		model.addAttribute("searchResult", pav.content);
		model.addAttribute("moreResults", !pav.isLast);
		
		// Columna lateral
		List<ArticleView> lastArticles = ArticleView.castList( articleService.findFirstNumber(5, false), commentRepository );
		List<CategoryView> categories = CategoryView.castList( CategoryCommons.getCategoryList() );
		List<CommentView> lastComments = CommentView.castList( commentService.getLastComments(5) );
		List<ArticleView> otherArticles = ArticleView.castList( articleService.findRandom(4, false) );
		
		model.addAttribute("categories", categories);
		model.addAttribute("last_comments", lastComments);
		model.addAttribute("last_articles", lastArticles);
		model.addAttribute("other_articles", otherArticles);

		return new ModelAndView("search");
	}
	
}

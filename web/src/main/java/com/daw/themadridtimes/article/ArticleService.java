package com.daw.themadridtimes.article;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.daw.themadridtimes.user.User;
import com.daw.themadridtimes.user.UserComponent;
import com.daw.themadridtimes.webconfig.Config;

@Service
public class ArticleService {

	public static interface View extends Article.Basic, ArticleView.Basic, User.Basic, Config.Responses {}
	public static interface Popular extends Article.Basic, User.Basic, Config.Responses {}
	public static interface Editor extends Article.Basic, Article.Details, User.Basic, Config.Responses {}
	public static interface ArticlesFavs extends CategoryFavourites.Basic, Article.Basic, User.Basic, Config.Responses {}

	@Autowired
	protected ArticleRepository articleRepository;
	
	@Autowired
	protected UserComponent userComponent;
	
	protected static final int ITEMS_LIST = 10; // Numero de articulos por pagina
	
	
	// Añadir visualizacion al contador
	public Article addView(Article a) {
		if(a == null) return null;
		
		a.addView();
		return articleRepository.save(a);
	}

	// Obtener datos de un articulo
	public Article get(long id, boolean contentHtml) {
		Article a = articleRepository.findOne(id);
		
		if(contentHtml)
			a.setContent( MarkdownConverter.getFormatedHtml( a.getContent() ) );
		
		return a;
	}
	
	// Devuelve una pagina de articulos para una categoria determinada
	// Recordar que las paginas empiezan en 0
	public Page<Article> getArticlesByCategory(String categoryId, int page, int number) {
		Page<Article> p = articleRepository.findByCategoryAndVisible(categoryId, true, new PageRequest(page, number));
		return p;
	}

	// Guardar cambios
	public Article save(Article a) {
		return articleRepository.save(a);
	}
	
	// Comprobar si tiene permisos de edicion
	public boolean hasPermission(User user, Article a) {
		return (user.equals( a.getAuthor() ) || user.hasRole("ROLE_ADMIN"));
	}

	// Elimiar articulo
	public void delete(Article a) {
		articleRepository.delete(a);
	}
	
	// Lista de articulos
	public Page<Article> listWhenPermission(int nPage) {
		User user = userComponent.getLoggedUser();
		Page<Article> page;
		
		if(user.hasRole("ROLE_ADMIN")) {
			page = articleRepository.findAll( new PageRequest(nPage, ITEMS_LIST, Sort.Direction.DESC, "id") );
		} else {
			User userLogged = userComponent.getLoggedUser();
			page = articleRepository.findByAuthor( userLogged, new PageRequest(nPage, ITEMS_LIST, Sort.Direction.DESC, "id") );
		}
		
		return page;
	}

	/**
	 * Ultimos X articulos publicados y que esten marcados como visibles
	 */
	public List<Article> findFirst6ByCategory(String category, boolean contentHtml) {
		List<Article> l = articleRepository.findFirst6ByCategoryAndVisible(category, true);
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}
	
	public List<Article> findFirstNumber(int number, boolean contentHtml) {
		List<Article> l = articleRepository.findFirstNumberAndVisible(number);
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}

	/**
	 * Obtener X articulos aleatorios
	 */
	public List<Article> findRandom(int number, boolean contentHtml) {
		List<Article> l = articleRepository.findRandomAndVisible(number);
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}

	public List<Article> findRandom4ThisWeek(boolean contentHtml) {
		List<Article> l = articleRepository.findRandom4ThisWeekAndVisible();
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}
	
	/**
	 * Obtener el ultimo articulo publicado de cada categoria
	 */
	public List<Article> findFirstEachCategory(boolean contentHtml) {
		List<Article> l = articleRepository.findFirstEachCategoryAndVisible();
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}

	/**
	 * Obtener los 2 articulos mas vistos durante la ultima semana
	 */
	public List<Article> find2PopularLastWeek(boolean contentHtml) {
		List<Article> l = articleRepository.find2PopularLastWeekAndVisible();
		if(contentHtml) this.MarkdownFormatedHtml(l);
		return l;
	}
	
	/*
	 * FUNCIONES AUXILIARES
	 */
	protected void MarkdownFormatedHtml(List<Article> l) {
		Iterator<Article> it = l.iterator();
		
		while(it.hasNext()) {
			Article a = it.next();
			a.setContent( MarkdownConverter.getFormatedHtml( a.getContent() ) );
		}
	}
}

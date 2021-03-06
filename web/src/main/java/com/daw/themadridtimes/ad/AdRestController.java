package com.daw.themadridtimes.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class AdRestController {

	@Autowired
	protected AdService adService;
	
	
	/**
	 * Anuncio al azar
	 */
	@JsonView(AdService.Basic.class)
	@RequestMapping(value="/anuncio", method=RequestMethod.GET)
	public ResponseEntity<Object> getRandom() {
		Ad ad = adService.getRandom();
		
		if(ad == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(ad, HttpStatus.OK);
	}

	/**
	 * Datos basicos de un anuncio
	 */
	@JsonView(AdService.Basic.class)
	@RequestMapping(value="/anuncio/{id}", method=RequestMethod.GET)
	public ResponseEntity<Object> get(@PathVariable long id) {
		Ad ad = adService.get(id);
		
		if(ad == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(ad, HttpStatus.OK);
	}

	/**
	 * Añade visualizacion a un anuncio
	 */
	@JsonView(AdService.Basic.class)
	@RequestMapping(value="/anuncio/{id}/visualizacion", method=RequestMethod.GET)
	public ResponseEntity<Object> addView(@PathVariable long id) {
		Ad ad = adService.get(id);
		if(ad == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		ad = adService.addView(ad);
		
		return new ResponseEntity<>(ad, HttpStatus.OK);
	}

	/**
	 * Añade click a un anuncio
	 */
	@JsonView(AdService.Basic.class)
	@RequestMapping(value="/anuncio/{id}/click", method=RequestMethod.GET)
	public ResponseEntity<Object> addClick(@PathVariable long id) {
		Ad ad = adService.get(id);
		if(ad == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		ad = adService.addClick(ad);
		
		return new ResponseEntity<>(ad, HttpStatus.OK);
	}
}

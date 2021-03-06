package com.daw.themadridtimes.preferences;

import com.daw.themadridtimes.favourite.Favourite;
import com.daw.themadridtimes.files.FileUploadCommons;
import com.daw.themadridtimes.requests.ApiDataUser;
import com.daw.themadridtimes.requests.ApiUserPassword;
import com.daw.themadridtimes.user.User;
import com.daw.themadridtimes.user.UserService;
import com.daw.themadridtimes.utils.Message;
import com.daw.themadridtimes.webconfig.Config;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PreferencesRestController {

    @Autowired
    protected UserService userService;
    
    @Autowired
    protected Config config;


    /**
     * Obtener configuracion actual del usuario logeado
     */
    @JsonView(UserService.UserDetails.class)
    @RequestMapping(value="/ajustes", method=RequestMethod.GET)
    public ResponseEntity<Object> get() {
        User u = userService.getLoggedUser();
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    /**
     * Modificar configuracion del usuario
     */
    @JsonView(UserService.UserDetails.class)
    @RequestMapping(value="/ajustes", method=RequestMethod.PUT)
    public ResponseEntity<Object> saveData(@RequestBody ApiDataUser r) {
    	Message message;
        User u = userService.getLoggedUser();
		
		// Validacion
		message = r.validation();
		if(message.getCode() != 0) return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		
		// Guardar datos
		u.setAlias(r.getAlias());
		u.setName(r.getName());
		u.setLastname(r.getLastname());
		u.setEmail(r.getEmail());
		
		u.setRoles(r.getRoles());
		
		Favourite fav = r.getFavourites();
		fav.setId( u.getFavourites().getId() );
		u.setFavourites(fav);
		
		u.setSex(r.getSex());
		u.setCity(r.getCity());
		u.setCountry(r.getCountry());
		u.setPhone(r.getPhoneNumber());
		u.setDescription(r.getDescription());
		u.setPersonalWeb(r.getPersonalWeb());
		
		u = userService.save(u);
		
		return new ResponseEntity<>(u, HttpStatus.OK);
    }
    
    @JsonView(UserService.UserDetails.class)
    @RequestMapping(value="/ajustes/imagen", method=RequestMethod.POST)
    public ResponseEntity<Object> saveImage(@RequestParam(name="file") MultipartFile file) {
    	User userLogged = userService.getLoggedUser();
    	
    	boolean result = FileUploadCommons.saveImage( file, config.getPathImgUsers(), String.valueOf(userLogged.getId()) );
    	if(!result)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<>(userLogged, HttpStatus.OK);
    }

    @JsonView(UserService.UserDetails.class)
    @RequestMapping(value="/ajustes/contrasena", method=RequestMethod.PUT)
    public ResponseEntity<Object> savePassword(@RequestBody ApiUserPassword r) {
    	Message message = r.validation();
    	if(message.getCode() != 0)
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

        User oldUser = userService.getLoggedUser();

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if(!bcrypt.matches( r.getPass_now(), oldUser.getPasswordHash())) {
        	message.setCode(1);
        	message.setMessage("La contraseña actual no coincide con la que tiene en estos momentos");
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        
        // Guardar contraseña
        oldUser.setPasswordHash(r.getPass_new());
        User u = userService.save(oldUser);
        
		return new ResponseEntity<>(u, HttpStatus.OK);
    }
}

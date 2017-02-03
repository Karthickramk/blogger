package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.auth.jwt.impl.JWT;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.cisco.blogger.dao.BlogDao;
import com.cisco.blogger.dao.UserDao;
import com.cisco.blogger.model.Blog;
import com.cisco.blogger.model.LoggedInUser;
import com.cisco.blogger.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
public class UserVerticle extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	BlogDao blogDao;
	
	UserDao userDao;
	
	@Autowired
	private Validator validator;
	JsonObject config;
	JWTAuth provider;
	String keystorePass;
	
	public UserVerticle(ApplicationContext context) {
		blogDao = (BlogDao) context.getBean("blogDao");
		userDao = (UserDao) context.getBean("userDao");
	}
	public UserVerticle(){
		
	}
	@Override
	public void start() throws Exception {
		keystorePass="secret";
		KeyStore keyStore= KeyStore.getInstance("JCEKS");
		InputStream fin1 = (this.getClass().getClassLoader().getResourceAsStream("keystore.jceks"));
		keyStore.load(fin1, keystorePass.toCharArray());
		Key key = keyStore.getKey("HS256", keystorePass.toCharArray());
		
		/** Logging in **/
		vertx.eventBus().consumer("com.cisco.user.login",message -> {
			User userObj = Json.decodeValue(message.body().toString(), User.class);
			System.out.println(userObj.toString());
			Optional<User> userOptional = userDao.getUserById(userObj.getUserName());
			boolean loginStatus = false;
			User user = null;
			System.out.println("userOptional-->"+userOptional);
			if(userOptional.isPresent()) {
				user = userOptional.get();
				System.out.print(user.toString());
				if (user.getPassword().equals(userObj.getPassword())) {
					
					loginStatus=true;
					System.out.println("loginstatus"+loginStatus);
				}	
			}
			if (loginStatus) {

				String compactJws = Jwts.builder()
				  .setSubject(user.getUserName())
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
				System.out.println("compactJws-->" + compactJws);
				
				String userparsed = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject();
				System.out.println("userparsed-->"+userparsed);
				message.reply(Json.encodePrettily(compactJws));
				
			} else {
				System.out.println("loginstatus"+loginStatus);

				message.reply(loginStatus);
			}
			
		});
		
		vertx.eventBus().consumer("com.cisco.user.validate",message -> {
			try {
				String token = message.body().toString();
				String userparsed = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
				System.out.println("userparsed-->"+userparsed);
				message.reply(userparsed);
				
			} catch (Exception e) {
				message.reply(false);
			}
		});
		
	}
	
}

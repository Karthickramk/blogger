package com.cisco.blogger.vertx;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Optional;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.cisco.blogger.dao.BlogDao;
import com.cisco.blogger.dao.MessageDao;
import com.cisco.blogger.dao.UserDao;
import com.cisco.blogger.model.User;
public class UserVerticle extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	BlogDao blogDao;
	
	UserDao userDao;
	
	MessageDao messageDao;
	
	@Autowired
	private Validator validator;
	JsonObject config;
	JWTAuth provider;
	String keystorePass;
	
	public UserVerticle(ApplicationContext context) {
		blogDao = (BlogDao) context.getBean("blogDao");
		userDao = (UserDao) context.getBean("userDao");
		messageDao = (MessageDao) context.getBean("messageDao");
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
			Optional<User> userOptional = userDao.getUserById(userObj.getUserName());
			boolean loginStatus = false;
			User user = null;
			logger.info("userOptional-->"+userOptional);
			if(userOptional.isPresent()) {
				user = userOptional.get();
				System.out.print(user.toString());
				if (user.getPassword().equals(userObj.getPassword())) {
					
					loginStatus=true;
					logger.info("loginstatus"+loginStatus);
				}	
			}
			if (loginStatus) {

				String compactJws = Jwts.builder()
				  .setSubject(user.getUserName())
				  .signWith(SignatureAlgorithm.HS512, key)
				  .compact();
				logger.info("compactJws-->" + compactJws);
				
				String userparsed = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject();
				logger.info("userparsed-->"+userparsed);

				
				message.reply(Json.encodePrettily(compactJws));
				
			} else {
				logger.info("loginstatus"+loginStatus);

				message.reply(loginStatus);
			}
			
		});
		
		vertx.eventBus().consumer("com.cisco.user.validate",message -> {
			try {
				String token = message.body().toString();
				String userparsed = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
				logger.info("userparsed-->"+userparsed);
				message.reply(userparsed);
				
			} catch (Exception e) {
				message.reply("failed");
			}
		});
		
	}
	
}

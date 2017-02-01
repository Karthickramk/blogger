package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;

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

public class DatabaseVerticle extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	BlogDao blogDao;
	
	UserDao userDao;
	
	@Autowired
	private Validator validator;
	
	public DatabaseVerticle(ApplicationContext context) {
		blogDao = (BlogDao) context.getBean("blogDao");
		userDao = (UserDao) context.getBean("userDao");
	}
	public DatabaseVerticle(){
		
	}
	@Override
	public void start() throws Exception {
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByTitle", message -> {
			Blog blog = blogDao.getBlogByTitle(message.body().toString());
			logger.info(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.addBlog", message -> {
			Blog blog = Json.decodeValue(message.body().toString(), Blog.class);
			blog.setCreatedDate(new Date());
			blog.setCreatedBy("admin");
			blogDao.addBlog(blog);
			logger.info(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.update", message -> {
			Blog blog = Json.decodeValue(message.body().toString(), Blog.class);
			blogDao.updateBlog(blog);
			logger.info(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.delete", message -> {
			Blog blog = blogDao.getBlogByTitle(message.body().toString());
			blogDao.deleteBlog(blog);
			logger.info(Json.encodePrettily(blog));
			message.reply("deleted");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getAllBlogs", message -> {
			List<Blog> blogs = blogDao.getAllBlogs();
			logger.info(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
			logger.info("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogsByUser", message -> {
			List<Blog> blogs = blogDao.getAllBlogs();
			logger.info(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
			logger.info("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByUser", message -> {
			List<Blog> blogs = blogDao.getAllBlogsByUser(message.body().toString());
			message.reply(Json.encodePrettily(blogs));
			logger.info("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogsByTag", message -> {
			List<Blog> blogs = blogDao.getAllBlogsByTag(message.body().toString());
			message.reply(Json.encodePrettily(blogs));
			logger.info("encoded");
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.register", message -> {
			User userObj = Json.decodeValue(message.body().toString(), User.class);
			logger.info("User object "+userObj);
			
			Set<ConstraintViolation<User>> constraintViolations = validator.validate(userObj);
			Set<String> messages = new HashSet<>(constraintViolations.size());
		    messages.addAll(constraintViolations.stream()
		            .map(constraintViolation -> String.format("%s", constraintViolation.getMessage()))
		            .collect(Collectors.toList()));
		    logger.info("messages "+messages);
		    
		    boolean isSuccess = false; 
		    // Valid registration details
		    if(constraintViolations.size()==0) {
		    	User user = Json.decodeValue(message.body().toString(), User.class);
				isSuccess = userDao.createUser(user);
		    } else {
		    	logger.info("Input is not valid");
		    }
		    
			message.reply(isSuccess);
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.get", message -> {
			String userName = message.body().toString();
			logger.info("GET User "+userName);
			Optional<User> userOptional = userDao.getUserById(userName);
			User user = null;
			if(userOptional.isPresent()) {
				user = userOptional.get();
			}
			logger.info("User info : "+user);
//			if(user==null) {
//				user = loginService.getCurrentUser();
//			}
			message.reply(Json.encodePrettily(user));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.list",message -> {
			List<User> users = userDao.getAllUsers();
			logger.info("Users list : "+users);
			message.reply(Json.encodePrettily(users));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.login",message -> {
			LoggedInUser user = new LoggedInUser("asdsad","asdsads");
			logger.info(Json.encodePrettily(user));
			message.reply(Json.encodePrettily(user));
		});
		
	}
	
}

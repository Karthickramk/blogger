package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.cisco.blogger.dao.BlogDao;
import com.cisco.blogger.dao.MessageDao;
import com.cisco.blogger.dao.UserDao;
import com.cisco.blogger.model.Blog;
import com.cisco.blogger.model.BlogComment;
import com.cisco.blogger.model.Message;
import com.cisco.blogger.model.User;

public class DatabaseVerticle extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	BlogDao blogDao;
	
	UserDao userDao;
	
	MessageDao messageDao;
	
	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	JsonObject config;
	JWTAuth provider;
	
	ApplicationContext context;
	
	public DatabaseVerticle(ApplicationContext context) {
		blogDao = (BlogDao) context.getBean("blogDao");
		userDao = (UserDao) context.getBean("userDao");
		messageDao = (MessageDao) context.getBean("messageDao");
	}
	public DatabaseVerticle(){
		
	}
	@Override
	public void start() throws Exception {
		config = new JsonObject().put("keyStore", new JsonObject()
			    .put("path", "keystore.jceks")
			    .put("type", "jceks")
			    .put("password", "secret"));
		 provider = JWTAuth.create(vertx, config);
		 
		vertx.eventBus().consumer("com.cisco.blogger.getAllBlogs", message -> {
				List<Blog> blogs = blogDao.getAllBlogs();
				logger.info(Json.encodePrettily(blogs));
				message.reply(Json.encodePrettily(blogs));
		});
		 
		vertx.eventBus().consumer("com.cisco.blogger.getBlogUniqueTags", message -> {
			logger.info("karthick");
				JsonObject tags = blogDao.getUniqueTags();
				logger.info(Json.encodePrettily(tags));
				message.reply(Json.encodePrettily(tags));
		});
		 
		vertx.eventBus().consumer("com.cisco.blogger.getFavBlogs", message -> {
			List<Blog> blogs = blogDao.getFavouriteBlogs(message.body().toString());
			logger.info(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getBlogsByUser", message -> {
			List<Blog> blogs = blogDao.getAllBlogs();
			logger.info(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByUser", message -> {
			List<Blog> blogs = blogDao.getAllBlogsByUser(message.body().toString());
			message.reply(Json.encodePrettily(blogs));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getBlogsByTag", message -> {
			List<String> tags = Arrays.asList(message.body().toString().split(","));
			List<Blog> blogs = blogDao.getAllBlogsByTag(tags);
			message.reply(Json.encodePrettily(blogs));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByTitle", message -> {
			Blog blog = blogDao.getBlogByTitle(message.body().toString());
			message.reply(Json.encodePrettily(blog));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getBlogById", message -> {
			Blog blog = blogDao.getBlogById(message.body().toString());
			message.reply(Json.encodePrettily(blog));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.addBlog", message -> {
			Blog blog = Json.decodeValue(message.body().toString(), Blog.class);
			blog.setCreatedDate(new Date());
			blog.setCreatedBy("admin");
			blogDao.addBlog(blog);
			message.reply(Json.encodePrettily(blog));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.updateBlog", message -> {
			JsonObject obj = new JsonObject(message.body().toString());
			String blogId = obj.getString("id");
			logger.info("Incoming blog id------->"+blogId);
			
			String userName = obj.getString("userName");
			String comment = obj.getString("comment");
			Blog original = blogDao.getBlogById(blogId);
			logger.info("Original Object retrieved from database------->"+original.get_id());
			List<BlogComment> comments = original.getComments();
			if(comments == null){
				comments = new ArrayList<BlogComment>();
			}
			BlogComment newComment = new BlogComment();
			newComment.setComment(comment);
			newComment.setCommentDate(new Date());
			newComment.setCommentBy(userName);
			comments.add(newComment);
			original.setComments(comments);
			blogDao.updateComments(original);
			message.reply(Json.encodePrettily(original));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.setFav", message -> {
			JsonObject obj = new JsonObject(message.body().toString());
			String blogId = obj.getString("id");
			logger.info("Incoming blog id------->"+blogId);
			String userName = obj.getString("userName");
			Blog original = blogDao.getBlogById(blogId);
			logger.info("Original Object retrieved from database------->"+original.get_id());
			List<String> favUsers = original.getUsersMarkedFavourites();
			if(favUsers == null){
				favUsers = new ArrayList<String>();
			}
			favUsers.add(userName);
			original.setUsersMarkedFavourites(favUsers);
			blogDao.setFavourite(original);
			message.reply(Json.encodePrettily(original));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.setUnFav", message -> {
			JsonObject obj = new JsonObject(message.body().toString());
			String blogId = obj.getString("id");
			logger.info("Incoming blog id------->"+blogId);
			String userName = obj.getString("userName");
			Blog original = blogDao.getBlogById(blogId);
			logger.info("Original Object retrieved from database------->"+original.get_id());
			List<String> favUsers = original.getUsersMarkedFavourites();
			if(favUsers != null){
				favUsers.remove(userName);
			}
			original.setUsersMarkedFavourites(favUsers);
			blogDao.setFavourite(original);
			message.reply(Json.encodePrettily(original));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.delete", message -> {
			Blog blog = blogDao.getBlogByTitle(message.body().toString());
			blogDao.deleteBlog(blog);
			message.reply("deleted");
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.getMessages", message -> {
			List<Message> msgs = messageDao.getAllMessages();
			message.reply(Json.encodePrettily(msgs));
		});
		vertx.eventBus().consumer("com.cisco.blogger.addMessages", message -> {
			Message msg = Json.decodeValue(message.body().toString(), Message.class);
			msg.setMessageDate(new Date());
			messageDao.addMessages(msg);
			message.reply(Json.encodePrettily(msg));
		});
		vertx.eventBus().consumer("com.cisco.blogger.user.register", message -> {
			User userObj = Json.decodeValue(message.body().toString(), User.class);
			
			Set<ConstraintViolation<User>> constraintViolations = validator.validate(userObj);
			StringBuffer str = new StringBuffer();
			for(ConstraintViolation constraint : constraintViolations){
				if(str.length() != 0){
					str.append(", ");
				}
				str.append(String.format("%s", constraint.getMessage()));
			}
		    logger.info("messages "+str.toString());
		    
		    boolean isSuccess = false; 
		    // Valid registration details
		    if(constraintViolations.size()==0) {
				isSuccess = userDao.createUser(userObj);
				message.reply(isSuccess);
		    } else {
		    	logger.info("Input is not valid");
		    	message.reply(str.toString());
		    }
		    
			
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.info.update", message -> {
	    	User user = Json.decodeValue(message.body().toString(), User.class);
	    	Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
			StringBuffer str = new StringBuffer();
			for(ConstraintViolation constraint : constraintViolations){
				if(str.length() != 0){
					str.append(", ");
				}
				str.append(String.format("%s", constraint.getMessage()));
			}
		    logger.info("messages "+str.toString());
		    
		    // Valid registration details
		    if(constraintViolations.size()==0) {
		    	userDao.updateUser(user);
				message.reply(true);
		    } else {
		    	logger.info("Input is not valid");
		    	message.reply(str.toString());
		    }
			
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.get", message -> {
			String userName = message.body().toString();
			Optional<User> userOptional = userDao.getUserById(userName);
			User user = null;
			if(userOptional.isPresent()) {
				user = userOptional.get();
			}
			message.reply(Json.encodePrettily(user));
		});
		
		vertx.eventBus().consumer("com.cisco.blogger.user.list",message -> {
			List<User> users = userDao.getAllUsers();
			message.reply(Json.encodePrettily(users));
		});
		
	}
	
}

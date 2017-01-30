package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;




import java.util.Date;
import java.util.List;




import com.cisco.blogger.dao.BlogDao;
import com.cisco.blogger.model.Blog;

public class DatabaseVerticle extends AbstractVerticle {
	BlogDao dao = new BlogDao();
	@Override
	public void start() throws Exception {
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByTitle", message -> {
			Blog blog = dao.getBlogByTitle(message.body().toString());
			System.out.println(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.addBlog", message -> {
			Blog blog = Json.decodeValue(message.body().toString(), Blog.class);
			blog.setCreatedDate(new Date());
			blog.setCreatedBy("admin");
			dao.addBlog(blog);
			System.out.println(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.update", message -> {
			Blog blog = Json.decodeValue(message.body().toString(), Blog.class);
			dao.updateBlog(blog);
			System.out.println(Json.encodePrettily(blog));
			message.reply(Json.encodePrettily(blog));
		});
		vertx.eventBus().consumer("com.cisco.blogger.delete", message -> {
			Blog blog = dao.getBlogByTitle(message.body().toString());
			dao.deleteBlog(blog);
			System.out.println(Json.encodePrettily(blog));
			message.reply("deleted");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getAllBlogs", message -> {
			List<Blog> blogs = dao.getAllBlogs();
			System.out.println(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
			System.out.println("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogsByUser", message -> {
			List<Blog> blogs = dao.getAllBlogs();
			System.out.println(Json.encodePrettily(blogs));
			message.reply(Json.encodePrettily(blogs));
			System.out.println("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByUser", message -> {
			List<Blog> blogs = dao.getAllBlogsByUser(message.body().toString());
			message.reply(Json.encodePrettily(blogs));
			System.out.println("encoded");
		});
		vertx.eventBus().consumer("com.cisco.blogger.getBlogByTag", message -> {
			List<Blog> blogs = dao.getAllBlogsByTag(message.body().toString());
			message.reply(Json.encodePrettily(blogs));
			System.out.println("encoded");
		});
	}
	
}

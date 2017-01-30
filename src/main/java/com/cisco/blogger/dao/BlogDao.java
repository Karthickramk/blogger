package com.cisco.blogger.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import com.cisco.blogger.model.Blog;
import com.mongodb.MongoClient;

public class BlogDao {
	Datastore store;
	public BlogDao(){
		MongoClient mongo = new MongoClient("localhost", 27017);
		Morphia morphia = new Morphia();
		store = morphia.createDatastore(mongo, "cisco_blogger1");
	}
	public void addBlog(Blog blog) {
		BasicDAO<Blog, Integer> dao = new BasicDAO<>(Blog.class, store);
		dao.save(blog);
	}
	public Blog getBlogByTitle(String title) {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		Blog blog = dao.createQuery().field("title").equal(title).get();
		System.out.println(blog);
		return blog;
	}
	public void updateBlog(Blog blog) {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		dao.save(blog);
	}
	public void deleteBlog(Blog blog) {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		dao.delete(blog);
	}
	public List<Blog> getAllBlogs() {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		List<Blog> blogs = dao.createQuery().asList();
		return blogs;
	}
	public List<Blog> getAllBlogsByUser(String user) {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		List<Blog> blogs = dao.createQuery().field("createdBy").equal(user).asList();;
		return blogs;
	}
	public List<Blog> getAllBlogsByTag(String tag) {
		BasicDAO<Blog, String> dao = new BasicDAO<>(Blog.class, store);
		List<Blog> blogs = dao.createQuery().field("tags").contains(tag).asList();;
		return blogs;
	}
}
package com.cisco.blogger.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.blogger.model.Blog;

@Service
public class BlogDao extends BasicDAO<Blog, Integer> {
	Datastore store;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	public BlogDao(Class<Blog> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	public void addBlog(Blog blog) {
		save(blog);
	}
	public Blog getBlogByTitle(String title) {
		Blog blog = createQuery().field("title").equal(title).get();
		return blog;
	}
	public void updateBlog(Blog blog) {
		save(blog);
	}
	public void deleteBlog(Blog blog) {
		delete(blog);
	}
	public List<Blog> getAllBlogs() {
		List<Blog> blogs = createQuery().asList();
		return blogs;
	}
	public List<Blog> getAllBlogsByUser(String user) {
		List<Blog> blogs = createQuery().field("createdBy").equal(user).asList();;
		return blogs;
	}
	public List<Blog> getAllBlogsByTag(String tag) {
		List<Blog> blogs = createQuery().field("tags").contains(tag).asList();;
		return blogs;
	}
}
package com.cisco.blogger.dao;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.blogger.model.Blog;
import com.mongodb.WriteResult;

@Service
public class BlogDao extends BasicDAO<Blog, Integer> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	public BlogDao(Class<Blog> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	public void addBlog(Blog blog) {
		save(blog);
	}
	
	public List<Blog> getAllBlogs() {
		Sort s = new Sort("createdDate",-1);
		List<Blog> blogs = createQuery().order(s).asList();
		return blogs;
	}
	
	public List<Blog> getAllBlogsByUser(String user) {
		Sort s = new Sort("createdDate",1);
		List<Blog> blogs = createQuery().field("createdBy").equal(user).order(s).asList();
		return blogs;
	}
	
	public List<Blog> getAllBlogsByTag(List<String> tag) {
		Sort s = new Sort("createdDate",-1);
		List<Blog> blogs = createQuery().field("tags").hasAnyOf(tag).order(s).asList();
		return blogs;
	}
	
	public List<Blog> getFavouriteBlogs(String userId) {
		Sort s = new Sort("createdDate",-1);
		return createQuery().field("usersMarkedFavourites").contains(userId).order(s).asList();
	}

	public Blog getBlogByTitle(String title) {
		return createQuery().field("title").equal(title).get();
	}
	public Blog getBlogById(String id) {
	    ObjectId oid = new ObjectId(id);
	    Blog blog = getDs().find(Blog.class).field("_id").equal(oid).get();
		return blog;
	}
	
	public void deleteBlog(Blog blog) {
		delete(blog);
	}
	
	public boolean deleteAllBlogs() {
		WriteResult result = deleteByQuery(createQuery());
		return result.wasAcknowledged();
	}
	
	public void updateComments(Blog blog) {
		Query<Blog> query = createQuery().field("_id").equal(new ObjectId(blog.get_id()));
		logger.info("Found blog with id ["+blog.get_id()+"] as "+query.get());
		UpdateOperations<Blog> ops = createUpdateOperations().set("comments", blog.getComments());
		update(query, ops);
	}
	
	public void setFavourite(Blog blog) {
		Query<Blog> query = createQuery().field("_id").equal(new ObjectId(blog.get_id()));
		logger.info("Found blog with id ["+blog.get_id()+"] as "+query.get());
		UpdateOperations<Blog> ops = createUpdateOperations().set("usersMarkedFavourites", blog.getUsersMarkedFavourites());
		update(query, ops);
	}
	
	public JsonObject getUniqueTags(){
		JsonObject obj = new JsonObject();
		Sort s = new Sort("createdDate",-1);
		List<Blog> blogs = createQuery().order(s).asList();
		Stream<Blog> stream = blogs.stream();
		List<String> tags = stream.flatMap(e->e.getTags().stream()).distinct().collect(Collectors.toList());
		obj.put("tags", tags);
		logger.info(obj.toString());
		return obj;
	}
}
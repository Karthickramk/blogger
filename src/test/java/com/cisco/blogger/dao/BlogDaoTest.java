package com.cisco.blogger.dao;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cisco.blogger.config.ApplicationTestConfiguration;
import com.cisco.blogger.model.Blog;
import com.cisco.blogger.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ApplicationTestConfiguration.class)
public class BlogDaoTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BlogDao blogDao;
	
	@Autowired
	private UserDao userDao;
	final String userName = "test";
	Blog blog;
	User user;
	@Before
	public void setup(){
		createBlogObj();
		createUserObj();
	}
	
	public void createBlogObj(){
		blog = new Blog();
		blog.setBlogContent("Test");
		blog.setCreatedBy("test");
		blog.setTitle("test");
		List<String> tags = new ArrayList<String>();
		tags.add("Java");
		blog.setTags(tags);
	}
	
	public void createUserObj(){
		user = new User();
		user.setEmail("test@test.com");
		user.setFirstName("testFirstName");
		user.setLastName("testLastName");
		user.setPassword("test");
		user.setPhoneNumber("1234567890");
		user.setUserName("test");
	}
	
	@After
	public void tearDown(){
		userDao.deleteUser(userName);
		blogDao.deleteAllBlogs();
	}
	
	@Test
	public void createBlogTest() {
		blogDao.addBlog(blog);
		Blog fromDb = blogDao.getBlogById(blog.get_id());
		Assert.assertEquals(blog.get_id(), fromDb.get_id());
		Assert.assertEquals(blog.getTitle(), fromDb.getTitle());
		blogDao.deleteAllBlogs();
		Assert.assertNull(blogDao.getBlogById(fromDb.get_id()));
	}
	
	@Test
	public void setFavouriteTest() {
		userDao.createUser(user);
		
		List<String> usersMarkedFavourites = new ArrayList<String>();
		usersMarkedFavourites.add(user.getUserName());
		blog.setUsersMarkedFavourites(usersMarkedFavourites);
		
		blogDao.addBlog(blog);

		Blog fromDb = blogDao.getBlogById(blog.get_id());
		Assert.assertEquals(blog.get_id(), fromDb.get_id());
		Assert.assertEquals(blog.getTitle(), fromDb.getTitle());
		blogDao.deleteAllBlogs();
		Assert.assertNull(blogDao.getBlogById(blog.get_id()));
	}
	
	
	@Test
	public void getAllBlogs() {
		List<Blog> allBlogs = blogDao.getAllBlogs();
		logger.info("blogs : "+allBlogs);
		Assert.assertEquals(0, allBlogs.size());
	}
	@Test
	public void getUniqueTags(){
		blogDao.addBlog(blog);
		List<String> tags = new ArrayList<String>();
		tags.add("j2ee");
		tags.add("html");
		tags.add("java");
		tags.add("Java");
		tags.add("java");
		tags.add("javascript");
		
		blog.setTags(tags);
		blogDao.addBlog(blog);
		JsonObject json = blogDao.getUniqueTags();
		JsonArray tagsList = (JsonArray) json.getValue("tags");
		tagsList.stream().forEach(t -> System.out.println("tag"+t));
		blogDao.deleteAllBlogs();
	}
	
}

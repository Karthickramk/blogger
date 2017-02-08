package com.cisco.blogger.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cisco.blogger.config.ApplicationTestConfiguration;
import com.cisco.blogger.model.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ApplicationTestConfiguration.class)
public class UserDaoTest {
	
	@Autowired
	private UserDao userDao;
	User user;
	final String userName = "test";
	@Before
	public void setup(){
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
	}
	
	@Test
	public void createUserTest() {
		Assert.assertTrue(userDao.createUser(user));
		Assert.assertFalse(userDao.createUser(user));
		userDao.deleteUser(userName);
	}
	
	@Test
	public void getUserByNameTest() {
		Assert.assertTrue(userDao.createUser(user));
		User fromDb = userDao.getUserById(user.getUserName()).get();
		Assert.assertEquals(user.getUserName(), fromDb.getUserName());
		Assert.assertEquals(user.getFirstName(), fromDb.getFirstName());
		userDao.deleteUser(userName);
	}
	
	@Test
	public void isUserNameAvailableTest() {
		Assert.assertTrue(userDao.isUserIdAvailable(userName));
		Assert.assertTrue(userDao.createUser(user));
		Assert.assertFalse(userDao.isUserIdAvailable(userName));
		userDao.deleteUser(userName);
	}
	
}

package com.cisco.blogger.dao;

import java.util.List;
import java.util.Optional;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.blogger.model.User;
import com.mongodb.WriteResult;

@Service
public class UserDao extends BasicDAO<User, Integer> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public UserDao(Class<User> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public Key<User> save(User entity) {
		Key<User> save = super.save(entity);
		return null;
	};
	
	public boolean createUser(User user) {
		boolean isSuccess = false;
		if(isUserIdAvailable(user.getUserName())) {
			save(user);
			isSuccess = true;
		} else {
			logger.info("User exists with name : "+user.getUserName());
		}
		return isSuccess;
	}
	
	public boolean deleteUser(String userId) {
		WriteResult result = deleteByQuery(createQuery().field("userName").equal(userId));
		return result.wasAcknowledged();
	}
	
	public boolean deleteAllUsers() {
		WriteResult result = deleteByQuery(createQuery());
		return result.wasAcknowledged();
	}
	
	public Optional<User> getUserById(String userId) {
		User user = createQuery().field("userName").equal(userId).get();
		return user!=null?Optional.of(user):Optional.empty();
	}

	public List<User> getAllUsers() {
		return createQuery().asList();
	}
	
	public List<String> getAllRegisteredUserNames() {
		return getCollection().distinct("userName");
	}
	
	public boolean isUserIdAvailable(String userId) {
		if(createQuery().field("userName").equal(userId).count()!=0) {
			return false;
		} else {
			return true;
		}
	}

	public long getUserCount() {
		return count();
	}

	public boolean isValid(String userName, String password) {
		return createQuery().field("userName").equal(userName).field("password").equal(password).count()==1;
	}

}

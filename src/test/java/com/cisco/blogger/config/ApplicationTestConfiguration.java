package com.cisco.blogger.config;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.cisco.blogger.model.Blog;
import com.cisco.blogger.model.Message;
import com.cisco.blogger.model.User;
import com.mongodb.MongoClient;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan({"com.cisco.blogger.vertx",
				"com.cisco.blogger.dao"})
public class ApplicationTestConfiguration {

	@Bean(name="validator")
	@Autowired
	public Validator getValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

	@Bean(name="datastore")
	@Autowired
	public Datastore getDataStore() {
		Datastore store = getMorphia().createDatastore(getMongoClient(), "cisco_blogger3_test");
		return store;
	}
	
	@Bean(name="mongo")
	@Autowired
	public MongoClient getMongoClient() {
		return new MongoClient("10.106.9.90", 27017);
	}
	
	@Bean(name="morphia")
	@Autowired
	public Morphia getMorphia() {
		return new Morphia();
	}
	
	@Bean
	public Class<User> getUserClass() {
		return User.class;
	}
	
	@Bean
	public Class<Blog> getBlogClass() {
		return Blog.class;
	}
	
	@Bean
	public Class<Message> getMessageClass() {
		return Message.class;
	}
	
}

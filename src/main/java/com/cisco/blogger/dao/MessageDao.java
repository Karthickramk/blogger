package com.cisco.blogger.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.blogger.model.Message;

@Service
public class MessageDao  extends BasicDAO<Message, Integer> {
	@Autowired
	public MessageDao(Class<Message> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public List<Message> getAllMessages() {
		List<Message> msgs = createQuery().asList();
		return msgs;
	}

	public void addMessages(Message msg) {
		save(msg);
	}
}

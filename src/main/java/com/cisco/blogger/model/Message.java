package com.cisco.blogger.model;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class Message {
	@Id
	private String id;
	private String messageBy;
	private Date messageDate;
	private String message;
	public String getMessageBy() {
		return messageBy;
	}
	public void setMessageBy(String messageBy) {
		this.messageBy = messageBy;
	}
	public Date getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}

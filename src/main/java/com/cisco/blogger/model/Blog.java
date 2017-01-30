package com.cisco.blogger.model;

import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Id;

public class Blog {
	@Id
	private String _id;
	private String title;
	private String blogContent;
	private List<BlogComment> comments;
	private List<String> tags;
	private List<String> authorizedUsers;
	private Date createdDate;
	private String createdBy;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBlogContent() {
		return blogContent;
	}
	public void setBlogContent(String blogContent) {
		this.blogContent = blogContent;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<String> getAuthorizedUsers() {
		return authorizedUsers;
	}
	public void setAuthorizedUsers(List<String> authorizedUsers) {
		this.authorizedUsers = authorizedUsers;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public List<BlogComment> getComments() {
		return comments;
	}
	public void setComments(List<BlogComment> comments) {
		this.comments = comments;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}

package com.cisco.blogger.model;

import java.util.List;

public class User {

	private String name;
	private List<Blog> favouriteBlogs;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Blog> getFavouriteBlogs() {
		return favouriteBlogs;
	}
	public void setFavouriteBlogs(List<Blog> favouriteBlogs) {
		this.favouriteBlogs = favouriteBlogs;
	}
	
}

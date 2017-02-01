package com.cisco.blogger.model;

public class LoggedInUser {

	private String userName;
	private String token;
	public LoggedInUser(String userName,String token){
		this.userName = userName;
		this.token = token;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}

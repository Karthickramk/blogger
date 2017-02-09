package com.cisco.blogger.vertx;


public class JWTHeaderTokenExtractor  {  
    public static String HEADER_PREFIX = "Bearer ";

    public static String extract(String header) {
    	return header.substring(HEADER_PREFIX.length(), header.length());
    }
}
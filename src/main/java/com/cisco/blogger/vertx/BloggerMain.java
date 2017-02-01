package com.cisco.blogger.vertx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.cisco.blogger.config.ApplicationConfiguration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class BloggerMain {
	
	public static void main(String[] args) {
		final Vertx vertx = Vertx.factory.vertx();
		ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
		
		vertx.deployVerticle(new BlogVerticle(context));
		vertx.deployVerticle(new DatabaseVerticle(context), new DeploymentOptions().setWorker(true));
		
	}

}

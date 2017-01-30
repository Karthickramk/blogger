package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class BlogVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> future) throws Exception {
		System.out.println("starting...");
		Router router = Router.router(vertx);
		vertx.deployVerticle("com.cisco.blogger.vertx.DatabaseVerticle", new DeploymentOptions().setWorker(true));
		router.route("/about").handler(rctx -> {
			HttpServerResponse response = rctx.response();
			response.putHeader("content-type", "text/html")
					.end("<h1>Hello from my first Vert.x 3 application via routers</h1>");
		});
		router.route("/static/*").handler(StaticHandler.create("web"));
		router.get("/api/blog/:title").handler(rctx -> {
			String title = rctx.request().getParam("title");
			vertx.eventBus().send("com.cisco.blogger.getBlogByTitle", title, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.get("/api/user/blog/:user").handler(rctx -> {
			String title = rctx.request().getParam("user");
			vertx.eventBus().send("com.cisco.blogger.getBlogsByUser", title, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.get("/api/blog").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.getAllBlogs", "", r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});

		router.route("/api/blog").handler(BodyHandler.create());
		router.post("/api/blog").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.addBlog", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.put("/api/blog/update").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.update", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(r));
			});
		});
		router.delete("/api/blog/:title").handler(rctx -> {
			String title = rctx.request().getParam("title");
			vertx.eventBus().send("com.cisco.blogger.delete", title, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "text/html; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.get("/api/tag/blog/:tag").handler(rctx -> {
			String tag = rctx.request().getParam("tag");
			vertx.eventBus().send("com.cisco.blogger.getBlogsByTag", tag, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});
	}

	@Override
	public void stop() throws Exception {
		System.out.println("stopping...");
		super.stop();
	}
}

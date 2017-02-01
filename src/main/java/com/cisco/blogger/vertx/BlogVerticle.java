package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


import com.cisco.blogger.model.LoggedInUser;

public class BlogVerticle extends AbstractVerticle {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public BlogVerticle(ApplicationContext context) {
	}

	@Override
	public void start(Future<Void> future) throws Exception {
		logger.info("starting...");
		Router router = Router.router(vertx);
		vertx.deployVerticle("com.cisco.blogger.vertx.DatabaseVerticle", new DeploymentOptions().setWorker(true));
		router.route("/about").handler(rctx -> {
			HttpServerResponse response = rctx.response();
			response.putHeader("content-type", "text/html")
					.end("<h1>Hello from my first Vert.x 3 application via routers</h1>");
		});
		router.route("/static/*").handler(StaticHandler.create("web"));
		router.route("/api/login").handler(BodyHandler.create());
		router.post("/api/login").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.login", rctx.getBodyAsJson(), r -> {
				Session session = rctx.session();
				  // Put some data from the session
				  session.put("username", rctx.getBodyAsJson().getString("uname"));
				  session.put("token", r.result().body().toString());
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
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
		
		router.get("/api/users").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.user.list","", r -> {
				rctx.response().setStatusCode(200).end(r.result().body().toString());
			});
		});
		
		router.get("/api/user/info/:username").handler(rctx -> {
			String userName = rctx.request().getParam("username");
			vertx.eventBus().send("com.cisco.blogger.user.get",userName, r -> {
				rctx.response().setStatusCode(200).end(r.result().body().toString());
			});
		});
		
		router.get("/my-pretty-notfound-handler").handler(ctx -> {
			  ctx.response()
			          .setStatusCode(404)
			          .end("NOT FOUND fancy html here!!!");
		});
		router.route("/api/resgister").handler(BodyHandler.create());
		router.post("/api/resgister").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.user.resgister", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.get().failureHandler(ctx -> {
		  if (ctx.statusCode() == 404) {
		    ctx.reroute("/my-pretty-notfound-handler");
		  } else {
		    ctx.next();
		  }
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
		logger.info("stopping...");
		super.stop();
	}
}

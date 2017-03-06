package com.cisco.blogger.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class BlogVerticle extends AbstractVerticle {

	private Logger logger = LoggerFactory.getLogger(getClass());
	public static final String JWT_TOKEN_HEADER_PARAM = "X-Authorization";
	
	public BlogVerticle(ApplicationContext context) {
	}

	@Override
	public void start(Future<Void> future) throws Exception {
		logger.info("starting...");
		Router router = Router.router(vertx);
		
		router.route("/about").handler(rctx -> {
			HttpServerResponse response = rctx.response();
			response.putHeader("content-type", "text/html")
					.end("<h1>Hello from my first Vert.x 3 application via routers</h1>");
		});
		router.route("/static/*").handler(StaticHandler.create("web"));
		router.route("/api/*").handler(BodyHandler.create());
		router.route("/unprotected/*").handler(BodyHandler.create());

		/** Register a user **/
		router.post("/unprotected/register").handler(rctx -> {
			logger.info("Registration request received.");
			vertx.eventBus().send("com.cisco.blogger.user.register", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		
		/** User Login **/
		router.post("/unprotected/login").handler(rctx -> {
			vertx.eventBus().send("com.cisco.user.login", rctx.getBodyAsJson(), r -> {
				JsonObject obj = new JsonObject(rctx.getBodyAsJson().toString());
				String userName = obj.getString("userName");
				logger.info("userName"+userName);		
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		/** Token Validation **/
		router.route("/api/*").handler(rctx -> {
			HttpServerResponse response = rctx.response();
			HttpServerRequest request = rctx.request();
			response.setChunked(true);
		   String tokenPayload = request.getHeader(JWT_TOKEN_HEADER_PARAM);
		   String token = JWTHeaderTokenExtractor.extract(tokenPayload);
			logger.info("Validating token:");
			vertx.eventBus().send("com.cisco.user.validate",token,  r -> {
				String reply = r.result().body().toString();
		        if (reply.equalsIgnoreCase("failed")) {
		        	logger.info("Token validation failed");
		        	rctx.response().setStatusCode(401).putHeader("content-type", "application/html; charset=utf-8")
		        		.end("Invalid token passed:");
		        } else {
		        	logger.info("Token validated successfully.Proceeding to next route");
		        	rctx.next();
		        }
			});
		});		
		router.get("/api/blog/:title").handler(rctx -> {
			String title = rctx.request().getParam("title");
			vertx.eventBus().send("com.cisco.blogger.getBlogByTitle", title, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		
		
		router.get("/api/blog/id/:id").handler(rctx -> {
			String id = rctx.request().getParam("id");
			vertx.eventBus().send("com.cisco.blogger.getBlogById", id, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		
		router.get("/api/user/blog/:user").handler(rctx -> {
			String user = rctx.request().getParam("user");
			vertx.eventBus().send("com.cisco.blogger.getBlogsByUser", user, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		router.get("/unprotected/tags").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.getBlogUniqueTags", "", r -> {
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
		
		router.get("/api/blog/getFav/:user").handler(rctx -> {
			String user = rctx.request().getParam("user");
			vertx.eventBus().send("com.cisco.blogger.getFavBlogs", user, r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});

		router.route("/api/blog").handler(BodyHandler.create());
		router.put("/api/blog").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.updateBlog", rctx.getBodyAsJson(), r -> {
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
		
		router.route("/api/user/info").handler(BodyHandler.create());
		router.put("/api/user/info").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.user.info.update", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
		
		router.get("/api/messages").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.getMessages","", r -> {
				rctx.response().setStatusCode(200).end(r.result().body().toString());
			});
		});
		
		router.route("/api/messages").handler(BodyHandler.create());
		router.post("/api/messages").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.addMessages",rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).end(r.result().body().toString());
			});
		});
	
		router.route("/api/blog/setFav").handler(BodyHandler.create());
		router.put("/api/blog/setFav").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.setFav", rctx.getBodyAsJson(), r -> {
				rctx.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(r.result().body().toString());
			});
		});
	
		router.route("/api/blog/setUnFav").handler(BodyHandler.create());
		router.put("/api/blog/setUnFav").handler(rctx -> {
			vertx.eventBus().send("com.cisco.blogger.setUnFav", rctx.getBodyAsJson(), r -> {
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
		
		vertx.createHttpServer().requestHandler(router::accept).websocketHandler(new Handler<ServerWebSocket>() {
			public void handle(final ServerWebSocket ws) {
		    	  if (ws.path().equals("/chat")) {
		    		  System.out.println("message recieved");
		    		  ws.handler(new Handler<Buffer>() {
		    			  public void handle(Buffer data){
		    				  System.out.println("message recieved"+data.toString());
		    				  vertx.eventBus().send("com.cisco.blogger.addMessages",data.toString(), r -> {
		    					  ws.writeFinalTextFrame(r.result().body().toString());
		    					});
		    			  }
					});
			       }
		      }
		    }).listen(config().getInteger("http.port", 8080),
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

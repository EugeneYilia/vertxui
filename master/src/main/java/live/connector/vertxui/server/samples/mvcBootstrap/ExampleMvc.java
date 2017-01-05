package live.connector.vertxui.server.samples.mvcBootstrap;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import live.connector.vertxui.client.samples.chatEventBus.Dto;
import live.connector.vertxui.client.samples.mvcBootstrap.View;
import live.connector.vertxui.server.samples.AllExamplesServer;
import live.connector.vertxui.server.transport.Pojofy;

public class ExampleMvc extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		router.route("/save").handler(Pojofy.ajax(Dto.class, this::serviceDoSomething));

		AllExamplesServer.startWarAndServer(View.class, router, server);
	}

	public Dto serviceDoSomething(HttpServerRequest request, Dto received) {
		log.info("Extra example: received a dto with action=" + request.getHeader("action") + " and color="
				+ received.color);
		return new Dto("red");
	}
}

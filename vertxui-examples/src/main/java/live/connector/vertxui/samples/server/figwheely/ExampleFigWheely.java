package live.connector.vertxui.samples.server.figwheely;

import java.lang.invoke.MethodHandles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import live.connector.vertxui.samples.client.AllExamplesClient;
import live.connector.vertxui.samples.client.figwheely.Client;
import live.connector.vertxui.samples.server.AllExamplesServer;
import live.connector.vertxui.server.FigWheely;

public class ExampleFigWheely extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

	@Override
	public void start() {
		// Initialize the router and a webserver with HTTP-compression
		Router router = Router.router(vertx);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

		// Start figwheely
		router.get(AllExamplesClient.figwheelyLocation).handler(FigWheely.create());

		// Serve folder assets-figwheely, and notify
		// clients of changes if figwheely is started (otherwise it is just a
		// normal StaticHandler).
		String url = "/sourcez/";
		router.get(url + "*").handler(FigWheely.staticHandler("assets/figwheely", url));

		AllExamplesServer.startWarAndServer(Client.class, router, server);
	}

}

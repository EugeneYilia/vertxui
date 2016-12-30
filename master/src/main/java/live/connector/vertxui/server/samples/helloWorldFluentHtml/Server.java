package live.connector.vertxui.server.samples.helloWorldFluentHtml;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.client.samples.helloWorldFluentHtml.Client;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class Server extends AllExamplesServer {

	public Server() {
		super(Client.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}

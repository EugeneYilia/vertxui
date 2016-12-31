package live.connector.vertxui.server.samples.mvc;

import java.lang.invoke.MethodHandles;

import io.vertx.core.Vertx;
import live.connector.vertxui.client.samples.mvc.View;
import live.connector.vertxui.server.samples.AllExamplesServer;

public class ExampleMvc extends AllExamplesServer {

	public ExampleMvc() {
		super(View.class);
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(MethodHandles.lookup().lookupClass().getName());
	}

}

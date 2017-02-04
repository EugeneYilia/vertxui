package live.connector.vertxui.samples.client.helloWorldFluentHtml;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.transport.Pojofy;
import live.connector.vertxui.samples.client.AllExamplesClient;
import live.connector.vertxui.samples.client.chatEventBus.Dto;

public class Client implements EntryPoint {

	public final static String url = "/ajax";

	private Fluent button;
	private Fluent response;
	private Fluent thinking;

	public Client() {
		button = body.div().button(null, "Click me!").id("hello-button").click(this::clicked);
		response = body.div();
		thinking = body.div().txt("The server waits as demonstration!").id("thinking-panel").css(Css.display, "none");
	}

	// It is advisable to write callbacks into methods, so you can easily write
	// jUnit tests.
	private void clicked(Event e) {
		button.disabled(true);
		thinking.css(Css.display, "");
		Pojofy.ajax("POST", url, null, null, null, this::responsed);
	}
	
	// It is advisable to write callbacks into methods, so you can easily write
	// jUnit tests.
	private void responsed(int responseCode, String text) {
		button.disabled(false);

		response.div().txt(text);
		thinking.css(Css.display, "none");

		// extra: POJO example
		Pojofy.ajax("POST", urlPojo, new Dto("white"), AllExamplesClient.dto, AllExamplesClient.dto,
				(i, a) -> console.log(a.color));
	}

	public final static String urlPojo = "/pojo";

	@Override
	public void onModuleLoad() {
	}

}

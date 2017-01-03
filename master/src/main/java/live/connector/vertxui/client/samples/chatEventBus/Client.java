package live.connector.vertxui.client.samples.chatEventBus;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import elemental.events.UIEvent;
import live.connector.vertxui.client.EventBus;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * @author Niels Gorisse
 *
 */

public class Client implements EntryPoint {

	// EventBus-address of text messages to everyone
	public static final String freeway = "twoWayAddressOpenedAtServerBridge";

	// EventBus-address of myDto objects
	public static final String serviceAddress = "serviceForMyDto";

	// EventBus mapper for the myDto object, for json-object-json conversion
	public interface MyDtoMapper extends ObjectMapper<MyDto> {
	}

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		EventBus eventBus = EventBus.create("http://localhost/chatEventbus", null);
		eventBus.onopen(evt -> {
			eventBus.publish(freeway, name + ": Ola, I'm " + name + ".", null);
			eventBus.registerHandler(freeway, null, (error, in) -> { // onmessage
				messages.li(in.get("body").asString());
			});

			// extra example: send example
			eventBus.send(freeway, name + ": I want a reply", null, (error, message) -> {
				console.log("server said: " + message.get("body"));
			});

			// extra example: send&receive object with automatic serialisation
			// Note: as an example we send and receive the same class, but you
			// can send and receive whatever you like.
			eventBus.send(serviceAddress, new MyDto("blue"), null, GWT.create(MyDtoMapper.class),
					GWT.create(MyDtoMapper.class), (MyDto object) -> {
						console.log("Server said: " + object.color);
					});
		});

		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				eventBus.publish(freeway, name + ": " + input.value(), null);
				input.value(""); // clear the inputfield
			}
		});
	}

	@Override
	public void onModuleLoad() {
	}

}

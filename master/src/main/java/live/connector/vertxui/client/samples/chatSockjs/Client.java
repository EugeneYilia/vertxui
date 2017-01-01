package live.connector.vertxui.client.samples.chatSockjs;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.window;

import com.google.gwt.core.client.EntryPoint;

import elemental.events.MessageEvent;
import elemental.events.UIEvent;
import live.connector.vertxui.client.SockJS;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * @author Niels Gorisse
 */

public class Client implements EntryPoint {

	public Client() {
		String name = window.prompt("What is your name?", "");

		Fluent input = body.input("text", "_");
		Fluent messages = body.div();

		SockJS socket = SockJS.create("http://localhost/chatSockjs");
		socket.setOnopen(e -> {
			socket.send(name + ": Ola, I'm " + name + ".");
		});
		socket.setOnmessage(e -> {
			messages.li(((MessageEvent) e).getData().toString());
		});
		input.keydown(evt -> {
			if (((UIEvent) evt).getKeyCode() == 13) {
				socket.send(name + ": " + input.value());
				input.value(""); // clear the inputfield
			}
		});
	}

	@Override
	public void onModuleLoad() {
	}
}

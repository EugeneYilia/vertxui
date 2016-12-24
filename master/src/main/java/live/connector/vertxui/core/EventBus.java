package live.connector.vertxui.core;

import org.teavm.flavour.json.test.TeaVMJSONRunner;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.typedarrays.Int32Array;

import io.vertx.core.Handler;
import live.connector.vertxui.fluentHtml.FluentHtml;

/**
 * A Vert.X sockJs wrapper for in javascript.
 * 
 * @author Niels Gorisse
 *
 */
public abstract class EventBus implements JSObject {

	static {
		FluentHtml.getHead().script("https://cdn.jsdelivr.net/sockjs/1.1.1/sockjs.min.js",
				"https://raw.githubusercontent.com/vert-x3/vertx-bus-bower/master/vertx-eventbus.js");
	}

	public static native Int32Array create(int length);

	@JSBody(params = { "address", "options" }, script = "return new EventBus(address,options);")
	public static native EventBus get(String address, String[] options);

	@JSProperty()
	public abstract void onopen(Handler<String> handler);

	@JSProperty()
	public abstract void registerHandler(String address, String[] headers, Handler<String> handler);

	@JSProperty()
	public abstract void unregisterHandler(String address, String[] headers, Handler<String> callback);

	@JSProperty()
	public abstract String publish(String address, String message, String[] headers);

	@JSProperty()
	public abstract void send(String address, String message, String[] headers, Handler<String> callback);

	@JSProperty()
	public abstract void onClose(Runnable object);

	@JSProperty()
	public abstract void onError(Handler<String> callback);

	/**
	 * Push a DTO to the server, where we registered this class to be received
	 * at the right place.
	 */
	public <T> void publish(T model) {
		publish(model.getClass().getName(), model);
	}

	public <T> void publish(String address, T model) {
		publish(model.getClass().getName(), TeaVMJSONRunner.serialize(model).asText(), null);
	}

	/**
	 * Consume a DTO from the server, where we registered this class to be sent
	 * to here.
	 * 
	 * @param classs
	 *            the class of the dto that needs to be sended.
	 * @param handler
	 *            a method that handles the dto
	 */
	public <T> void register(Class<T> classs, Handler<T> handler) {
		register(classs.getName(), null, handler);
	}

	public <T> void register(String address, Class<T> classs, Handler<T> handler) {
		registerHandler(address, null, string -> {
			handler.handle(TeaVMJSONRunner.deserialize(string, classs));
		});
	}

}

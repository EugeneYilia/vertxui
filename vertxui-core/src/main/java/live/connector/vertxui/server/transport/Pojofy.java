package live.connector.vertxui.server.transport;

import java.lang.invoke.MethodHandles;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.RoutingContext;
import live.connector.vertxui.server.VertxUI;

/**
 * A Vert.x helper class to communicate in POJO's for ajax, the eventbus and
 * websocket/sockjs.
 * 
 * Note: try to use vertx-jersey for a lot of ajax calls if you do not use
 * Vert.x as microservice, it is probably more efficient and does generate
 * cleaner code.
 * 
 * @author ng
 *
 */
public class Pojofy {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, BiFunction<A, RoutingContext, Object> handler) {
		return context -> {
			context.request().bodyHandler(body -> {
				A input = in(inputType, body.toString());
				String output = out(handler.apply(input, context));
				if (output == null) {
					// do nothing
				} else {
					context.response().putHeader("Content-Type", "application/json; charset=" + VertxUI.charset);
					context.response().end(output);
				}
			});
		};
	}

	/**
	 * An ajax call handler for a void method.
	 */
	/**
	 * @param <A>
	 *            the input type class
	 * @param inputType
	 *            the input type
	 * @param handler
	 *            the callback
	 * @return the webserver handler for this ajax call.
	 */
	public static <A> Handler<RoutingContext> ajax(Class<A> inputType, BiConsumer<A, RoutingContext> handler) {
		return context -> {

			// Internet Explorer 11 caches ajax calls
			context.response().putHeader("Cache-control", "none");
			context.response().putHeader("Pragma", "none");
			context.response().putHeader("Expires", "0");
			context.response().putHeader("Content-Type", "application/json; charset=" + VertxUI.charset);

			context.request().bodyHandler(body -> {
				context.response().end();
				handler.accept(in(inputType, body.toString()), context);
			});
		};
	}

	public static <A> void eventbus(String urlOrAddress, Class<A> inputType, BiFunction<A, MultiMap, Object> handler) {
		Vertx.currentContext().owner().eventBus().consumer(urlOrAddress, message -> {
			A input = in(inputType, (String) message.body());
			String output = out(handler.apply(input, message.headers()));
			if (output != null) {
				if (message.replyAddress() != null) {
					message.reply(output);
				} else {
					log.warning("reply not send, the client is not using send() but publish(), lost output=" + output);
				}
			}
		});
	}

	/**
	 * Note: replies at the same address!
	 * 
	 * @param <A>
	 *            the input type class
	 * @param <S>
	 *            the socket
	 * @param socket
	 *            the socket
	 * @param url
	 *            the url
	 * @param in
	 *            the input buffer
	 * @param inputType
	 *            the input class
	 * @param handler
	 *            the callback
	 * @return whether the input wqs successfully shallowed.
	 */
	public static <A, S extends WriteStream<Buffer>> boolean socket(S socket, String url, Buffer in, Class<A> inputType,
			BiFunction<A, JsonObject, Object> handler) {
		String start = ("{\"url\":\"" + url);
		if (in.length() < start.getBytes().length || !in.getString(0, start.length()).equals(start)) {
			return false;
		}

		JsonObject json = in.toJsonObject();
		A input = in(inputType, json.getString("body"));

		String output = out(handler.apply(input, json.getJsonObject("headers")));
		if (output == null) {
			return false;
		}
		json.put("body", output);
		json.remove("headers");

		if (socket instanceof WebSocket) {
			// prevents blobs at the javascript end, so is faster
			((WebSocket) socket).writeFinalTextFrame(json.toString());
		} else {
			socket.write(Buffer.buffer(json.toString()));
		}
		return true;
	}

	private static String out(Object output) {
		if (output == null) {
			return null;
		} else if (output instanceof String) {
			return (String) output;
		} else {
			return Json.encode(output);
		}
	}

	@SuppressWarnings("unchecked")
	private static <I> I in(Class<I> inputType, String in) {
		// no input type or null? use string
		if (in == null || in.isEmpty() || inputType == null || inputType.getClass().equals(String.class)) {
			return (I) in;
		} else {
			return (I) Json.decodeValue(in, inputType);
		}
	}

}

package live.connector.vertxui.server;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

public class FigWheelyVertX extends AbstractVerticle {

	private final static Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * Override target-dir when necessary, default is "target/classes/".
	 */
	public static String buildDir = "target/classes";
	public static int port = 8090;
	public static boolean started = false;
	protected static Router router;
	private static List<Watchable> watchables = new ArrayList<>();

	private static class Watchable {
		long lastModified;
		File file;
		String url;
		VertxUI handler;
		int urlNumber;

		@Override
		public String toString() {
			return new Gson().toJson(this).toString();
		}
	}

	protected static void addFile(File file, String url) {
		// log.info("add file " + file + " url="+url);
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.url = url;
		watchables.add(watchable);
	}

	public static boolean addVertX(File file, VertxUI handler) {
		// log.info("add vertx " + file);
		Watchable watchable = new Watchable();
		watchable.file = file;
		watchable.lastModified = file.lastModified();
		watchable.handler = handler;
		watchable.urlNumber = router.getRoutes().size();
		watchables.add(watchable);
		return true;
	}

	@Override
	public void start() {
		final String browserIds = "figwheelyEventBus";
		vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {
			@Override
			public void handle(final ServerWebSocket ws) {
				if (!ws.path().equals("/")) {
					ws.reject();
					return;
				}
				final String id = ws.textHandlerID();
				// log.info("welcoming " + id);
				vertx.sharedData().getLocalMap(browserIds).put(id, "whatever");
				ws.closeHandler(data -> {
					vertx.sharedData().getLocalMap(browserIds).remove(id);
				});
			}
		}).listen(port, listenHandler -> {
			if (listenHandler.failed()) {
				log.log(Level.SEVERE, "Startup error", listenHandler.cause());
				// stop on startup error
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						vertx.deploymentIDs().forEach(vertx::undeploy);
						vertx.close();
					}
				});
				System.exit(0);
			}
		});

		vertx.executeBlocking(future -> {
			while (true) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				for (Watchable watchable : watchables) {
					if (watchable.file.lastModified() != watchable.lastModified) {
						// log.info("Changed: " + watchable.url);
						watchable.lastModified = watchable.file.lastModified();
						try {
							String url = null;
							if (watchable.handler != null) {
								watchable.handler.sychronousReTranslate();
								url = router.getRoutes().get(watchable.urlNumber).getPath();
								url += "a/a.nocache.js"; // GWT
							} else {
								url = watchable.url;
							}
							// log.info("url=" + url);
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								log.info("reload: " + url);
								vertx.eventBus().send((String) obj, "reload: " + url);
							}
						} catch (IOException | InterruptedException e) {
							for (Object obj : vertx.sharedData().getLocalMap(browserIds).keySet()) {
								vertx.eventBus().send((String) obj, "error: " + e.getMessage());
							}
						}
					}
				}
			}
		}, result -> {
		});
	}

}
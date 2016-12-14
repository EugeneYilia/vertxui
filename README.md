vertx-ui
===

A [Vert.X](http://vertx.io/) pure-Java UI toolkit with ultrafast server-time Java to Javascript translation (by [TeaVM](http://teavm.org/)), a small fluid HTML toolkit, and automatic browser reloading called Figwheely. The Vert.X eventbus does not only stretchs all the way inside your browser (with SockJS websockets), but now also in the same programming language.

The server-time translation works at server startup time so you don't need to set up any Maven/IDE tools for developing, and you don't have your IDE being locked because it is doing 'something in the background' when you save a file (a nightmare you probably recognise with Maven or GWT projects).

You don't need file access at runtime, which makes vertx-ui ideal as minimal microservice. Heck, remember you don't need to setup Apache or Tomcat too because you're using Vert.X which can [handle thousands of connections per core](https://dzone.com/articles/inside-vertx-comparison-nodejs).

Using Java instead of Javascript means strong-typing, direct binding with entity classes, convenient tooling, easy junit testing, and both the Java �nd the JavaScript ecosystems under your fingertips.

Server-time translation does not mean you can not debug your code. To debug, use FigWheely which notifies browsers if the output of a VertxUI (or any other file) has changed.

### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. So, not only forget about javascript, but forget about editing html files too.

	router.route("/client").handler(new VertxUI(Client.class, true));

If you want the file to be automaticly reloaded when the classfile changes, put this on top of server code to turn on the wheel of figwheely: 

	FigWheely.with(router);
    
Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

The hello-world example translates from java to javascript within a second (and after that less) server startup time - that is probably less than you putting that file somewhere in the right folder. The result is one raw 68kb dependency-less javascript+html file, or a 16kb HTTP-zipped file. The resulting javascript is so small because TeaVM only translates from APIs that what was actually used.

### Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure 
[TeaVM](http://teavm.org/).

		HTMLButtonElement button = document.createElement("button").cast();
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.listenClick(evt -> clicked());
		body.appendChild(button);
		...
		
	private void clicked() {
		button.setDisabled(true);
		thinkingPanel.getStyle().setProperty("display", "");
		...
	}

## Clientside fluid HTML

You can also use fluid HTML, which is a lot shorter and more readable.

		Button button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		...
		
	private void clicked() {
		button.disable();
		thinkingPanel.css("display", "");
		...
	}

Of course you can mix with existing html and javascript by a document.getElement(theId):

	Div responses = Div.dom(theId);


## EventBus websocket at server and client in pure java 


This project is just a few weeks old - so hang on. I hope to have the eventbus access ready very very soon, it doesn't look that difficult to wrap an existing javascript API inside TeaVM.

A scetch I wrote before I started:

    public class Client extends VertxUI {
    
	// Model (inline as demonstration)
	public class Model {
		public String user;
		public String password;
	}
	
	private Model model = new Model();
	
	private Div title;
	
	public Client() {
		Html body = Html.body();
		
		// View
		Div title = body.div("<h1>Bla</h1>");
		Form form = body.form()
			.input("user", model.user, i -> {
			model.user = i;
		}).input("password", i -> {
			model.password = i;
		}).input("SEND", null, () -> {
			form.send();
		});
	
		// View-Controller binding
		form.post(GUI::send);
    	
		eventBus().register("response", GUI::receive);
	}
	
	// Controller
	private static void send(JsonObject updated) {
		eventBus().wrap("login", updated);
		title.inner("<h1>Sent!</h1>");
	}
	
	private void receive(JsonObject received) {
		title.inner("Received from the server: "+received);
	
	  }
    }


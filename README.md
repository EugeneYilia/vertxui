vertxui
===

A 100% Java 100% asynchronous toolkit (Vert.X and GWT elemental), with POJO serializers, Fluent HTML (with virtualDOM behind the scenes)), an Eventbus server and clientside, automatic browser reloading and more.

VertxUI offers:
* forget about URL's, just register and publish POJO's with ajax websockets sockjs or the eventbus.
* forget about HTML, just write fluent HTML.
* forget about Javascript, you're familiar with Java.
* forget about installing IDE tooling, the java to javascript translation happens run-time.
* during development: automatic browser reloading of generated javascript and other files (.css/.jpg) without browser refresh.
* Fluent html has a virtual DOM behind the scenes (a la ReactJS), only visually updating what changed in your model.
* websockets, sockjs and the VertX EventBus are available at server and browsers in the same language.

Pure-Java clientside (using GWT-elemental) means:
* strong-typed client-side Javascript
* use Java 8's lambda's and streams for client-side view and behavior (instead of pseudo-HTML like React and others)
* use the same DTO/entity classes and constants server-side and client-side.
* access to both the Java (threads etc) �nd the Javascript ecosystems
* easy junit testing of client-side code, and other convenient Java tooling
 
[Vert.X](http://vertx.io/) adds:
* probably the easiest and [fastest](https://dzone.com/articles/inside-vertx-comparison-nodejs) node.js-alike webserver
* no need for anything else: no Apache and Tomcat.
* the serverside EventBus, and a wonderful speedy async ecosystem.

Examples are included for: hello world (vanilla js and Fluent HTML), automatic browser reloading (Figwheely), 3 webchats with: websockets SockJS and EventBus, lots of POJO (de)serialization, TodoMVC, Bootstrap, jQuery Mobile and more.

### Serverside

The serverside is easy. This single line serves all necessary front-end Javascript code including the necessary (single-lined) wrapping HTML, ready to be shown in the browser. So, not only forget about javascript, but forget about editing html files too. Vert.X comes with HTTP compression out of the box so there is no need to do anything else except turning HTTP compression on (see all examples).

	router.route("/client").handler(new VertxUI(Client.class, true));

### Automatic browser reloading

Server-time translation does not mean you can not debug your code. To debug, just turn on FigWheely. 

If you want to speed up your development and not loose the browserstate by pressing reload, use FigWheely which automaticly ensures browsers reload changed javascript or any other file (.css .jpg etc). You will never want to write .css or behavior javascript without FigWheely:

		router.get("/figWheely.js").handler(FigWheely.create());
  
### Clientside pure DOM

The clientside looks like plain javascript but then with Java (8's lambda) callbacks. This is pure GWT elemental (previously TeaVM):

		button = document.createElement("button");
		button.setAttribute("id", "hello-button");
		button.setInnerHTML("Click me");
		button.setOnclick(evt -> clicked());
		body.appendChild(button);
		...
		
	private void clicked() {
		button.setAttribute("disabled", "");
		thinking.getStyle().setProperty("display", "");
		...
	}

### Clientside Fluent HTML

You can also use fluent HTML, which is a lot shorter and more readable. Don't worry about speed, fluent HTML uses a virtual DOM behind the scenes.

		Button button = body.button("Click me").id("hello-button").onClick(evt -> clicked());
		...
		
	private void clicked() {
		button.disabled(true);
		thinking.css(Style.display, "");
		...
	}

You can create state-aware Fluent HTML objects. Fluent Html only updates the components that were changed: Work in progress!

		response.add(model, m -> {
				 return Li("myClass").a(m.name, "/details?name=" + m.name);
			}
		});

		input.keyup(event -> {
			 model.name = input.value();
			 response.sync(); // re-render
		});

Use Java 8 lambda's and streams to write your user interface:

	Stream.of("apple","a").filter(a->a.length()>2).map(t -> new Li(t)).forEach(ul::add);

### EventBus at server and client in pure java gives beautiful MVC 

The eventbus is available in Java at both sides. This is just like in GWT, but then stretched out to _all_ browsers (a la socket.io) thanks to VertX. Just register the same DTO at clientside and serverside to be received or send. This is easier then also facilitating which service the DTO should go to, the server can work it out.

The model+view (browser):

	class Model {
		public String name;
	}

	class View {

	private Model model = new Model();
	private Div response;
	
	public View() {
		response = body.div();
		Input input = body.div().input("text", "aName");
		
		// Controller		
		input.keyUp(changed -> {
			model.name = input.value();
			Pojofy.ajax("POST", "/ajax", model, modelMap, null, (String s) -> console.log(s));
		});

	}

The controller (serverside) can be for example (ajax example):

		router.post("/ajax").handler(Pojofy.ajax(Model.class, (m, c) -> {
			log.info("Received a pojo from the client: color=" + m.color);
			return "a string";
		}));

Niels Gorisse

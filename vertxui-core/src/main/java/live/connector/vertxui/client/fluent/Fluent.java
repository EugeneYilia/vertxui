package live.connector.vertxui.client.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xhr.client.XMLHttpRequest;

import elemental.dom.Document;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.events.UIEvent;
import elemental.html.Console;
import elemental.html.InputElement;
import elemental.html.Window;
import elemental.js.dom.JsDocument;
import elemental.js.html.JsWindow;
import live.connector.vertxui.client.test.ConsoleTester;

/**
 * Fluent HTML, child-based fluent-basednotation of html. Use getDocument()
 * getBody() and getHead() to start building your GUI. Adding childs is done by
 * using the methods (like .li() ) or by some constructors that can handle
 * multiple arguments (like .div(li[]) ). Attributes are set by attr(), styles
 * by style(), and listeners by listen() or their appropriate methods.
 * 
 * @author ng
 *
 */
public class Fluent implements Viewable {

	static {
		if (!GWT.isClient()) {
			document = null;
			window = null;
		} else {
			document = getDocument();// Browser.getDocument();
			window = getWindow(); // Browser.getWindow();
		}
	}
	public static Document document;
	public final static Window window;
	public final static Console console;
	public static Fluent body;
	public final static Fluent head;
	static {
		if (!GWT.isClient()) {
			console = new ConsoleTester();
			body = new Fluent(null);
			head = new Fluent(null);
		} else {
			console = window.getConsole();
			body = new Fluent(document.getBody());
			head = new Fluent(document.getHead());
		}
	}

	private static native JsWindow getWindow() /*-{
												return window.top;
												}-*/;

	private static native JsDocument getDocument() /*-{
													return window.top.document;
														}-*/;

	/**
	 * If we are attached, this element exists, otherwise this is null (or we
	 * must be synced).
	 */
	protected Element element;
	protected Fluent parent;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	protected String tag;
	protected TreeMap<Att, String> attrs;
	protected TreeMap<Css, String> styles;
	protected TreeMap<String, EventListener> listeners;
	protected List<Viewable> childs;
	protected String inner;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 */
	private Fluent(String tag, Fluent parent) {
		this.tag = tag;
		this.parent = parent;

		// Update parent
		if (parent != null) {
			if (parent.childs == null) {
				parent.childs = new ArrayList<>();
			}
			parent.childs.add(this);
		}

		// Add to DOM if connected
		if (tag != null && parent != null && parent.element != null) {
			element = document.createElement(tag);
			if (parent != null) {
				// console.log("appending " + tag + " to parentTag: " +
				// parent.tag);
				parent.element.appendChild(element);
			}
		}
	}

	/**
	 * For when this represents an existing object: getBody() getHead() and
	 * dom().
	 * 
	 * @param parent
	 *            the existing object
	 */
	protected Fluent(Element parent) {
		element = parent;
	}

	/**
	 * Do not create but GET the dom object.
	 */
	public Element dom() {
		return element;
	}

	/**
	 * Set the inner text (HTML) for this element. Set to null (or empty string)
	 * to clear.
	 */
	public Fluent in(String innerText) {
		if (Renderer.equalsString(this.inner, innerText)) {
			// console.log("Skipping, still " + innerText);
			return this;
		}
		this.inner = innerText;
		if (element != null) {
			// console.log("setting innerText to "+innerText);
			element.setInnerText(innerText);
		}
		return this;
	}

	/**
	 * Gives the inner html that has been set; note that the real DOM inner HTML
	 * is "" if you set it to null or when no value is given anymore.
	 */
	public String in() {
		return this.inner;
	}

	/**
	 * Add or remove (by value null) an eventlistener.
	 * 
	 */

	public Fluent listen(String name, EventListener value) {
		if (listeners == null) {
			listeners = new TreeMap<>();
		}
		if (value != null) {
			listeners.put(name, value);
			if (element != null) {
				((Node) element).addEventListener(name, value);
			}
		} else { // remove
			EventListener oldValue = listeners.get(name);
			listeners.remove(name);
			if (element != null) {
				((Node) element).removeEventListener(name, oldValue);
			}
		}
		return this;
	}

	public EventListener listen(String event) {
		if (listeners == null) {
			return null;
		}
		return listeners.get(event);
	}

	public Fluent keyup(Consumer<KeyboardEvent> listener) {
		return listen(Event.KEYUP, evt -> {
			evt.stopPropagation();
			listener.accept((KeyboardEvent) evt);
		});
	}

	public Fluent click(Consumer<MouseEvent> listener) {
		return listen(Event.CLICK, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent load(EventListener listener) {
		return listen("LOAD", listener);
	}

	public Fluent focus(EventListener listener) {
		return listen(Event.FOCUS, listener);
	}

	public Fluent blur(Consumer<UIEvent> listener) {
		return listen(Event.BLUR, evt -> {
			evt.stopPropagation();
			listener.accept((UIEvent) evt);
		});
	}

	public Fluent keydown(Consumer<KeyboardEvent> listener) {
		return listen(Event.KEYDOWN, evt -> {
			evt.stopPropagation();
			listener.accept((KeyboardEvent) evt);
		});
	}

	public Fluent keypress(Consumer<KeyboardEvent> listener) {
		return listen(Event.KEYPRESS, evt -> {
			evt.stopPropagation();
			listener.accept((KeyboardEvent) evt);
		});
	}

	public Fluent dblclick(Consumer<MouseEvent> listener) {
		return listen(Event.DBLCLICK, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mousedown(Consumer<MouseEvent> listener) {
		return listen(Event.MOUSEDOWN, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mouseup(Consumer<MouseEvent> listener) {
		return listen(Event.MOUSEUP, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mouseover(Consumer<MouseEvent> listener) {
		return listen(Event.MOUSEOVER, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mouseenter(Consumer<MouseEvent> listener) {
		return listen("mouseenter", evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mouseleave(Consumer<MouseEvent> listener) {
		return listen("mouseleave", evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mousemove(Consumer<MouseEvent> listener) {
		return listen(Event.MOUSEMOVE, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent mouseout(Consumer<MouseEvent> listener) {
		return listen(Event.MOUSEOUT, evt -> {
			evt.stopPropagation();
			listener.accept((MouseEvent) evt);
		});
	}

	public Fluent css(Css name, String value, Css name2, String value2) {
		css(name, value);
		css(name2, value2);
		return this;
	}

	/**
	 * Set or remove (by value null) a css style.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Fluent css(Css name, String value) {

		if (styles == null) {
			styles = new TreeMap<>();
		}
		if (value == null) {
			styles.remove(name);
		} else {
			styles.put(name, value);
		}
		if (element != null) {
			if (value == null) {
				element.getStyle().removeProperty(name.nameValid());
			} else {
				element.getStyle().setProperty(name.nameValid(), value);
			}
		}
		return this;
	}

	public String css(Css name) {
		if (styles == null) {
			return null;
		}
		return styles.get(name);
	}

	public Fluent att(Att name, String value, Att name2, String value2) {
		return att(name, value).att(name2, value2);
	}

	public Fluent att(Att name, String value, Att name2, String value2, Att name3, String value3) {
		return att(name, value).att(name2, value2).att(name3, value3);
	}

	/**
	 * Set or remove (by value null) an attribute.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Fluent att(Att name, String value) {
		if (name == null) { // ignoring the call
			return this;
		}
		if (attrs == null) {
			attrs = new TreeMap<>();
		}
		if (value == null) {
			if (attrs.containsKey(name)) {
				attrs.remove(name);
				if (element != null) {
					element.removeAttribute(name.nameValid());
				}
			}
		} else {
			attrs.put(name, value);
			if (element != null) {
				element.setAttribute(name.nameValid(), value);
			}
		}
		return this;
	}

	public String attr(Att name) {
		if (attrs == null) {
			return null;
		}
		return attrs.get(name);
	}

	public String id() {
		return attr(Att.id);
	}

	public Fluent id(String string) {
		return att(Att.id, string);
	}

	public String classs() {
		return attr(Att.class_);
	}

	public Fluent classs(String string) {
		return att(Att.class_, string);
	}

	public String tag() {
		return tag;
	}

	private void addNew(Viewable item) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		if (item instanceof ViewOnBase) {
			((ViewOnBase) item).setParent(this);
			((ViewOnBase) item).sync(); // needs to render!
		} else {
			item = getRootOf((Fluent) item);
		}
		childs.add(item);
	}

	protected static Fluent getRootOf(Fluent item) {
		// When a Fluent created by a static function is given, we should
		// get the most upper parent, not the last item of the fluent
		// notated item.
		if (item == null) {
			return null;
		}
		while (item.parent != null) {
			if (item.element != null) {
				throw new IllegalArgumentException("Can not reconnect connected DOM elements");
			}
			item = item.parent;
		}
		return item;
	}

	private Fluent add(Fluent... items) {
		for (Fluent item : items) {
			addNew(item);
		}
		return this;
	}

	private Fluent add(Stream<Fluent> stream) {
		stream.forEach(item -> addNew(item));
		return this;
	}

	public List<Viewable> getChildren() {
		return childs;
	}

	public ViewOnBase add(ViewOnBase result) {
		result.setParent(this);
		addNew(result);
		return result;
	}

	public <T> ViewOn<T> add(T initialState, Function<T, Fluent> method) {
		ViewOn<T> result = new ViewOn<T>(initialState, method);
		add(result);
		return result;
	}

	public <A, B> ViewOnBoth<A, B> add(A initialState1, B initialState2, BiFunction<A, B, Fluent> method) {
		ViewOnBoth<A, B> result = new ViewOnBoth<A, B>(initialState1, initialState2, method);
		add(result);
		return result;
	}

	@Override
	public String toString() {
		String result = "Fluent{<";
		if (tag == null) {
			result += "null";
		} else {
			result += tag;
		}
		if (attrs != null) {
			for (Att attr : attrs.keySet()) {
				result += " " + attr.nameValid() + "=" + attrs.get(attr);
			}
		}
		result += " /> el=";
		if (element == null) {
			result += "null";
		} else {
			result += element.getNodeName();
		}
		result += ", parent.tag=";
		if (parent != null) {
			result += parent.tag;
		}
		result += "}";
		return result;
	}

	public Fluent hidden(boolean b) {
		element.setHidden(b);
		// if (hidden) {
		// css(Style.visibility, "hidden");
		// } else {
		// css(Style.visibility, "visible");
		// }
		return this;
	}

	public void disabled(boolean disabled) {
		if (disabled) {
			att(Att.disable, "");
		} else {
			att(Att.disable, null);
		}
	}

	/**
	 * Get the value of an input field.
	 */
	public String value() {
		return ((InputElement) element).getValue();
	}

	/**
	 * Set the value of an input field.
	 */
	public Fluent value(String value) {
		((InputElement) element).setValue("");
		return this;
	}

	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:

	public Fluent input() {
		return new Fluent("INPUT", this);
	}

	public Fluent input(String classs) {
		return input().classs(classs);
	}

	public Fluent input(String classs, String type) {
		return input().classs(classs).att(Att.type, type);
	}

	public Fluent input(String classs, String type, String id) {
		return input().classs(classs).att(Att.type, type).id(id);
	}

	public static Fluent Input() {
		return new Fluent("INPUT", null);
	}

	public static Fluent Input(String classs, String type) {
		return Input().classs(classs).att(Att.type, type);
	}

	public static Fluent Input(String classs, String type, String id) {
		return Input().classs(classs).att(Att.type, type).id(id);
	}

	public Fluent button() {
		return new Fluent("BUTTON", this);
	}

	public Fluent button(String classs) {
		return button().classs(classs);
	}

	public Fluent button(String classs, String text) {
		return button().classs(classs).in(text);
	}

	public static Fluent Button() {
		return new Fluent("BUTTON", null);
	}

	public static Fluent Button(String classs) {
		return Button().classs(classs);
	}

	public static Fluent Button(String classs, String text) {
		return Button().classs(classs).in(text);
	}

	public Fluent li() {
		return new Fluent("LI", this);
	}

	public Fluent li(String classs) {
		return li().classs(classs);
	}

	public Fluent li(String classs, String text) {
		return li(classs).in(text);
	}

	public Fluent li(Fluent... fluents) {
		return li().add(fluents);
	}

	public static Fluent Li() {
		return new Fluent("LI", null);
	}

	public static Fluent Li(String classs) {
		return Li().classs(classs);
	}

	public static Fluent Li(String classs, String inner) {
		return Li(classs).in(inner);
	}

	public static Fluent Li(Fluent... fluents) {
		return Li().add(fluents);
	}

	public Fluent div() {
		return new Fluent("DIV", this);
	}

	public Fluent div(String classs) {
		return div().classs(classs);
	}

	public Fluent div(String classs, String inner) {
		return div(classs).in(inner);
	}

	public Fluent div(Fluent... list) {
		return div().add(list);
	}

	public Fluent div(Stream<Fluent> stream) {
		return div().add(stream);
	}

	public Fluent div(String classs, String inner, Fluent... adds) {
		return div(classs).in(inner).add(adds);
	}

	public Fluent div(String classs, Stream<Fluent> stream) {
		return div(classs).add(stream);
	}

	public Fluent div(String classs, Fluent... adds) {
		return div(classs).add(adds);
	}

	public static Fluent Div() {
		return new Fluent("DIV", null);
	}

	public static Fluent Div(String classs) {
		return Div().classs(classs);
	}

	public static Fluent Div(String classs, String inner) {
		return Div().classs(classs).in(inner);
	}

	public static Fluent Div(Fluent... list) {
		return Div().add(list);
	}

	public static Fluent Div(String classs, Fluent... items) {
		return Div().classs(classs).add(items);
	}

	public static Fluent Div(String classs, Stream<Fluent> stream) {
		return Div(classs).add(stream);
	}

	// REST

	public Fluent area() {
		return new Fluent("AREA", this);
	}

	public Fluent base() {
		return new Fluent("BASE", this);
	}

	public Fluent br() {
		return new Fluent("BR", this);
	}

	public Fluent col() {
		return new Fluent("COL", this);
	}

	public Fluent embed() {
		return new Fluent("EMBED", this);
	}

	public Fluent hr() {
		return new Fluent("HR", this);
	}

	public Fluent img(String src) {
		Fluent result = new Fluent("IMG", this);
		result.att(Att.src, src);
		return result;
	}

	public Fluent keygen() {
		return new Fluent("KEYGEN", this);
	}

	public Fluent meta() {
		return new Fluent("META", this);
	}

	public Fluent param() {
		return new Fluent("PARAM", this);
	}

	public Fluent source() {
		return new Fluent("SOURCE", this);
	}

	public Fluent track() {
		return new Fluent("TRACK", this);
	}

	public Fluent wbr() {
		return new Fluent("WBR", this);
	}

	public Fluent a(String inner, String href) {
		return new Fluent("A", this).att(Att.href, href).in(inner);
	}

	public static Fluent A(String classs, String inner, String href) {
		return new Fluent("A", null).att(Att.href, href).classs(classs).in(inner);
	}

	public Fluent abbr() {
		return new Fluent("ABBR", this);
	}

	public Fluent address() {
		return new Fluent("ADDRESS", this);
	}

	public Fluent article() {
		return new Fluent("ARTICLE", this);
	}

	public Fluent aside() {
		return new Fluent("ASIDE", this);
	}

	public Fluent audio() {
		return new Fluent("AUDIO", this);
	}

	public Fluent b() {
		return new Fluent("B", this);
	}

	public Fluent bdi() {
		return new Fluent("BDI", this);
	}

	public Fluent bdo() {
		return new Fluent("BDO", this);
	}

	public Fluent blockquote() {
		return new Fluent("BLOCKQUOTE", this);
	}

	public Fluent body() {
		return new Fluent("BODY", this);
	}

	public Fluent canvas() {
		return new Fluent("CANVAS", this);
	}

	public Fluent caption() {
		return new Fluent("CAPTION", this);
	}

	public Fluent cite() {
		return new Fluent("CITE", this);
	}

	public Fluent code() {
		return new Fluent("CODE", this);
	}

	public Fluent colgroup() {
		return new Fluent("COLGROUP", this);
	}

	public Fluent datalist() {
		return new Fluent("DATALIST", this);
	}

	public Fluent dd() {
		return new Fluent("DD", this);
	}

	public Fluent del() {
		return new Fluent("DEL", this);
	}

	public Fluent details() {
		return new Fluent("DETAILS", this);
	}

	public Fluent dfn() {
		return new Fluent("DFN", this);
	}

	public Fluent dialog() {
		return new Fluent("DIALOG", this);
	}

	public Fluent dl() {
		return new Fluent("DL", this);
	}

	public Fluent dt() {
		return new Fluent("DT", this);
	}

	public Fluent em() {
		return new Fluent("EM", this);
	}

	public Fluent fieldset() {
		return new Fluent("FIELDSET", this);
	}

	public Fluent figcaption() {
		return new Fluent("FIGCAPTION", this);
	}

	public Fluent figure() {
		return new Fluent("FIGURE", this);
	}

	public Fluent footer() {
		return new Fluent("FOOTER", this);
	}

	public Fluent form() {
		return new Fluent("FORM", this);
	}

	public Fluent form(String classs) {
		return form().classs(classs);
	}

	public static Fluent Form() {
		return new Fluent("FORM", null);
	}

	public static Fluent Form(String classs) {
		return Form().classs(classs);
	}

	public Fluent h1() {
		return new Fluent("H1", this);
	}

	public Fluent h1(String classs, String text) {
		return h1().classs(classs).in(text);
	}

	public static Fluent H1(String classs, String text) {
		return new Fluent("H1", null).classs(classs).in(text);
	}

	public Fluent h2() {
		return new Fluent("H2", this);
	}

	public Fluent h3() {
		return new Fluent("H3", this);
	}

	public Fluent h4() {
		return new Fluent("H4", this);
	}

	public Fluent h5() {
		return new Fluent("H5", this);
	}

	public Fluent h6() {
		return new Fluent("H6", this);
	}

	public Fluent header() {
		return new Fluent("HEADER", this);
	}

	public Fluent i() {
		return new Fluent("I", this);
	}

	public Fluent iframe() {
		return new Fluent("IFRAME", this);
	}

	public Fluent ins() {
		return new Fluent("INS", this);
	}

	public Fluent kbd() {
		return new Fluent("KBD", this);
	}

	public Fluent label() {
		return new Fluent("LABEL", this);
	}

	public Fluent label(Fluent... fluents) {
		return label().add(fluents);
	}

	public Fluent label(String classs) {
		return label().classs(classs);
	}

	public Fluent label(String classs, String inner) {
		return label().classs(classs).in(inner);
	}

	public static Fluent Label() {
		return new Fluent("LABEL", null);
	}

	public static Fluent Label(String classs) {
		return Label().classs(classs);
	}

	public static Fluent Label(String classs, String inner) {
		return Label().classs(classs).in(inner);
	}

	public Fluent legend() {
		return new Fluent("LEGEND", this);
	}

	public Fluent main() {
		return new Fluent("MAIN", this);
	}

	public Fluent map() {
		return new Fluent("MAP", this);
	}

	public Fluent mark() {
		return new Fluent("MARK", this);
	}

	public Fluent menu() {
		return new Fluent("MENU", this);
	}

	public Fluent menuitem() {
		return new Fluent("MENUITEM", this);
	}

	public Fluent meter() {
		return new Fluent("METER", this);
	}

	public Fluent nav() {
		return new Fluent("NAV", this);
	}

	public Fluent nav(String classs) {
		return nav().classs(classs);
	}

	public Fluent noscript() {
		return new Fluent("NOSCRIPT", this);
	}

	public Fluent object() {
		return new Fluent("OBJECT", this);
	}

	public Fluent ol() {
		return new Fluent("OL", this);
	}

	public Fluent optgroup() {
		return new Fluent("OPTGROUP", this);
	}

	public Fluent option() {
		return new Fluent("OPTION", this);
	}

	public static Fluent Option() {
		return new Fluent("OPTION", null);
	}

	public static Fluent Option(String classs, String inner) {
		return Option().classs(classs).in(inner);
	}

	public Fluent output() {
		return new Fluent("OUTPUT", this);
	}

	public Fluent p() {
		return new Fluent("P", this);
	}

	public Fluent p(String classs, String text) {
		return p().classs(classs).in(text);
	}

	public Fluent pre(String classs, String text) {
		return new Fluent("PRE", this).classs(classs).in(text);
	}

	public Fluent progress() {
		return new Fluent("PROGRESS", this);
	}

	public Fluent q() {
		return new Fluent("Q", this);
	}

	public Fluent rp() {
		return new Fluent("RP", this);
	}

	public Fluent rt() {
		return new Fluent("RT", this);
	}

	public Fluent ruby() {
		return new Fluent("RUBY", this);
	}

	public Fluent s() {
		return new Fluent("S", this);
	}

	public Fluent samp() {
		return new Fluent("SAMP", this);
	}

	private static class XMLHttpRequestSyc extends XMLHttpRequest {
		protected XMLHttpRequestSyc() {
		}

		public final native void open(String httpMethod, String url, boolean sync) /*-{
																						this.open(httpMethod, url, sync);
																						}-*/;
	}

	public native static void eval(String code) /*-{
													window.top.eval(code);
													}-*/;

	/**
	 * Load javascript files synchronously and evalue/execute them directly too.
	 * You can also add them at the head of the html-document with
	 * Vertx.addLibrariesJs(), which is the same but more 'to the rules'. You
	 * need this if you want to use the javascript right after loading it (which
	 * is normal in most cases).
	 * 
	 * @return
	 */
	public Fluent scriptSync(String... jss) {
		if (!GWT.isClient()) {
			return this;
		}
		for (String js : jss) {
			XMLHttpRequestSyc xhr = (XMLHttpRequestSyc) XMLHttpRequestSyc.create();
			xhr.setOnReadyStateChange(a -> {
				if (a.getReadyState() == XMLHttpRequest.DONE && a.getStatus() == 200) {
					eval(xhr.getResponseText());
				}
			});
			xhr.open("GET", js, false);
			xhr.send();
		}
		return this;
	}

	/**
	 * Load one or more javascript files, asynchronous as normal. You can't use
	 * these libraries in your code directly, for that, use scriptAsyncEval().
	 * 
	 * @param jss
	 * @return
	 */
	public Fluent script(String... jss) {
		for (String js : jss) {
			new Fluent("script", this).att(Att.type, "text/javascript").att(Att.src, js);

			// This works too, is async
			// XMLHttpRequestSyc xhr = (XMLHttpRequestSyc)
			// XMLHttpRequestSyc.create();
			// xhr.setOnReadyStateChange(a -> {
			// if (a.getReadyState() == XMLHttpRequest.DONE && a.getStatus() ==
			// 200) {
			// new Fluent("script", this).inner(xhr.getResponseText());
			// // Element src = document.createElement("script");
			// // src.setAttribute("type", "text/javascript");
			// // // src.setAttribute("src", js);
			// // src.setInnerText(xhr.getResponseText());
			// // element.appendChild(src);
			// }
			// });
			// xhr.open("GET", js, false);
			// xhr.send();
		}
		return this;
	}

	public Fluent stylesheet(String... csss) {
		for (String css : csss) {
			new Fluent("link", this).att(Att.rel, "stylesheet").att(Att.href, css);
		}
		return this;
	}

	public Fluent section() {
		return new Fluent("SECTION", this);
	}

	public Fluent select() {
		return new Fluent("SELECT", this);
	}

	public static Fluent Select() {
		return new Fluent("SELECT", null);
	}

	public static Fluent Select(String classs) {
		return Select().classs(classs);
	}

	public static Fluent Select(String classs, Fluent... fluents) {
		return Select().classs(classs).add(fluents);
	}

	public Fluent small() {
		return new Fluent("SMALL", this);
	}

	public Fluent span() {
		return new Fluent("SPAN", this);
	}

	public Fluent span(String classs) {
		return span().classs(classs);
	}

	public Fluent span(String classs, String inner) {
		return span().classs(classs).in(inner);
	}

	public static Fluent Span() {
		return new Fluent("SPAN", null);
	}

	public static Fluent Span(String classs) {
		return Span().classs(classs);
	}

	public static Fluent Span(String classs, String inner) {
		return Span().classs(classs).in(inner);
	}

	public Fluent strong() {
		return new Fluent("STRONG", this);
	}

	public Fluent sub() {
		return new Fluent("SUB", this);
	}

	public Fluent summary() {
		return new Fluent("SUMMARY", this);
	}

	public Fluent sup() {
		return new Fluent("SUP", this);
	}

	public Fluent table() {
		return new Fluent("TABLE", this);
	}

	public Fluent table(String classs) {
		return table().classs(classs);
	}

	public static Fluent Table() {
		return new Fluent("TABLE", null);
	}

	public static Fluent Table(String classs) {
		return Table().classs(classs);
	}

	public Fluent tbody() {
		return new Fluent("TBODY", this);
	}

	public Fluent td() {
		return new Fluent("TD", this);
	}

	public Fluent td(String classs, String inner) {
		return td().classs(classs).in(inner);
	}

	public static Fluent Td() {
		return new Fluent("TD", null);
	}

	public static Fluent Td(String classs, String inner) {
		return Td().classs(classs).in(inner);
	}

	public Fluent textarea() {
		return new Fluent("TEXTAREA", this);
	}

	public Fluent tfoot() {
		return new Fluent("TFOOT", this);
	}

	public Fluent th() {
		return new Fluent("TH", this);
	}

	public Fluent thead() {
		return new Fluent("THEAD", this);
	}

	public Fluent time() {
		return new Fluent("TIME", this);
	}

	public Fluent title(String classs, String inner) {
		return new Fluent("TITLE", this).classs(classs).in(inner);
	}

	public Fluent tr() {
		return new Fluent("TR", this);
	}

	public Fluent tr(Fluent... tds) {
		return tr().add(tds);
	}

	public Fluent u() {
		return new Fluent("U", this);
	}

	public Fluent ul() {
		return new Fluent("UL", this);
	}

	public Fluent ul(String classs) {
		return ul().classs(classs);
	}

	public Fluent ul(String classs, Fluent... items) {
		return ul(classs).add(items);
	}

	public Fluent ul(Fluent... items) {
		return ul().add(items);
	}

	public Fluent ul(Stream<Fluent> stream) {
		return ul().add(stream);
	}

	public static Fluent Ul() {
		return new Fluent("UL", null);
	}

	public static Fluent Ul(String classs) {
		return Ul().classs(classs);
	}

	public static Fluent Ul(String classs, Fluent... items) {
		return Ul(classs).add(items);
	}

	public static Fluent Ul(Fluent... items) {
		return Ul().add(items);
	}

	public static Fluent Ul(Stream<Fluent> items) {
		return Ul().add(items);
	}

	public Fluent var() {
		return new Fluent("VAR", this);
	}

	public Fluent video() {
		return new Fluent("VIDEO", this);
	}

	// UNIT TESTING
	// UNIT TESTING
	// UNIT TESTING

	/**
	 * Clean the DOM manually before the next junit test.
	 */
	public static void clearVirtualDOM() {
		if (!GWT.isClient()) {
			body = new Fluent(null);
		} else {
			throw new IllegalArgumentException(
					"Calling this method has zero meaning inside your browser, reload the page in your browser for a clean start.");
		}
	}

	public Fluent clone() {
		if (parent != null || element != null) {
			throw new IllegalArgumentException(
					"You can only clone objects created with a static method (which start with a capital letter like Div or Span) and which are not DOM-connected yet.");
		}
		Fluent result = new Fluent(tag, null);
		if (inner != null) {
			result.in(inner);
		}
		if (attrs != null) {
			for (Att att : attrs.keySet()) {
				result.att(att, attrs.get(att));
			}
		}
		if (styles != null) {
			for (Css name : styles.keySet()) {
				result.css(name, styles.get(name));
			}
		}
		if (listeners != null) {
			for (String name : listeners.keySet()) {
				result.listen(name, listeners.get(name));
			}
		}
		if (childs != null) {
			for (Viewable child : childs) {
				if (child instanceof Fluent) {
					result.add(((Fluent) child).clone());
				} else if (child instanceof ViewOn<?>) {
					result.add(((ViewOn<?>) child).clone());
				} else {
					result.add(((ViewOnBoth<?, ?>) child).clone());
				}
			}
		}
		return result;
	}

	/*
	 * This does not cover listeners (which is not possible in GWT production
	 * mode), but does cover the rest, which should be enough to identify which
	 * child is which for most render optimalisation issues. For example, when
	 * one child between other childs in a list is deleted.
	 */
	public int getCrc() {
		StringBuilder result = new StringBuilder();
		if (tag != null) {
			result.append(tag);
		}
		if (inner != null) {
			result.append(inner);
		}
		if (attrs != null) {
			for (Att att : attrs.keySet()) {
				result.append(att.name());
			}
		}
		if (childs != null) {
			// only three deep
			byte[] cache = new byte[8];

			for (int x = 0; x < 3 && x < childs.size(); x++) {
				long child = childs.get(x).getCrc();
				for (int i = 7; i >= 0; i--) {
					cache[i] = (byte) (child & 0xFF);
					child >>= 8;
				}
				result.append(cache);
			}
		}
		return result.toString().hashCode();
	}

}
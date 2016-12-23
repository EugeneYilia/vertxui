package live.connector.vertxui.fluentHtml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.NamedNodeMap;

import live.connector.vertxui.reacty.ReactC;
import live.connector.vertxui.reacty.ReactM;
import live.connector.vertxui.reacty.ReactMC;
import live.connector.vertxui.streamy.Str;

/**
 * Create fluent HTML. // TODO Markup: +child: methode, +childs: constructor,
 * attrs: attr()
 * 
 * @author ng
 *
 */
public class FluentHtml {

	protected final static HTMLDocument document = Window.current().getDocument();

	// @JSBody(params = { "javascript" }, script = "javascript")
	// protected static native void javascript(String pureJs);

	/**
	 * If we are attached, this element exists, otherwise this is null (or we
	 * must be synced).
	 */
	protected HTMLElement element;

	/**
	 * Attached or detached: these are tag, attrs and children. If tag null, we
	 * are a non-API tag.
	 */
	private String tag;
	private Map<NameAttr, String> attrs;
	private Map<NameStyle, String> styles;
	private Map<NameListener, EventListener<?>> listeners;
	private List<FluentHtml> childs;
	private String inner;

	/**
	 * API call for normal HTML elements. Without a parent: detached.
	 */
	protected FluentHtml(String tag, FluentHtml parent) {
		this.tag = tag;
		if (tag != null) {
			element = document.createElement(tag);
			if (parent != null) {
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
	protected FluentHtml(HTMLElement parent) {
		element = parent;
	}

	/**
	 * Do not create but GET the body.
	 * 
	 */
	public static Body getBody() {
		return new Body(document.getBody());
	}

	/**
	 * Do not create but GET the head.
	 * 
	 */
	public static Head getHead() {
		return new Head(document.getHead());
	}

	/**
	 * Do not create but GET the dom object.
	 */
	public HTMLElement dom() {
		return element;
	}

	/**
	 * Get an existing object from the dom by 'id'. Please don't use this, it's
	 * slow.
	 * 
	 * @param id
	 *            the id
	 * @return a fluent html object
	 */

	@SuppressWarnings("unchecked")
	protected static <T extends FluentHtml> T dom(String id, Class<? extends FluentHtml> classs) {
		HTMLElement found = document.getElementById("id");
		if (!found.getTagName().equals(classs.getSimpleName().toLowerCase())) {
			throw new IllegalArgumentException("Requested non-existing dom with id=" + id + ": tagname="
					+ found.getTagName() + " requesting tagname=" + classs.getSimpleName().toLowerCase());
		}
		return (T) new FluentHtml(found);
	}

	public FluentHtml inner(String innerHtml) {
		this.inner = innerHtml;
		if (element != null) {
			element.setInnerHTML(innerHtml);
		}
		return this;
	}

	public String inner() {
		return this.inner;
	}

	public FluentHtml listen(NameListener event, EventListener<?> listener) {
		if (listeners == null) {
			listeners = new HashMap<>();
		}
		listeners.put(event, listener);
		if (element != null) {
			element.addEventListener(event.name(), listener);
		}
		return this;
	}

	public EventListener<?> listen(String event) {
		if (listeners == null) {
			return null;
		}
		return listeners.get(event);
	}

	public String css(NameStyle name) {
		if (styles == null) {
			return null;
		}
		return styles.get(name);
	}

	public FluentHtml css(NameStyle name, String value) {
		if (styles == null) {
			styles = new HashMap<>();
		}
		styles.put(name, value);
		if (element != null) {
			element.getStyle().setProperty(name.nameValid(), value);
		}
		return this;
	}

	public String attr(NameAttr name) {
		if (attrs == null) {
			return null;
		}
		return attrs.get(name);
	}

	public FluentHtml attr(NameAttr name, String value) {
		if (attrs == null) {
			attrs = new HashMap<>();
		}
		attrs.put(name, value);
		if (element != null) {
			element.setAttribute(name.nameValid(), value);
		}
		return this;
	}

	public String tag() {
		return tag;
	}

	public String id() {
		return attr(NameAttr.id);
	}

	public FluentHtml id(String string) {
		return attr(NameAttr.id, string);
	}

	public String classs() {
		return attr(NameAttr.class_);
	}

	public FluentHtml classs(String string) {
		return attr(NameAttr.class_, string);
	}

	public FluentHtml add(List<? extends FluentHtml> items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml add(FluentHtml[] items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml addd(FluentHtml... items) {
		for (FluentHtml item : items) {
			add(item);
		}
		return this;
	}

	public FluentHtml add(Str<FluentHtml> streamy) {
		add(streamy.collectToList());
		return this;
	}

	public <T> FluentHtml add(T model, ReactM<T> method) {
		return new ReactMC<T>(model, method);
	}

	public FluentHtml add(FluentHtml add) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		childs.add(add);
		sync(add);
		return this;
	}

	public void sync() {
		sync(this);
	}

	private void sync(FluentHtml add) {
		if (element == null) { // not attached: no rendering
			return;
		}
		while (add.tag == null) { // skip ReactC objects
			add = ((ReactC) add).generate();
		}
		if (add.element != null) { // add is already attached
			syncRender(add);
		} else {
			syncCreate(add);
		}
	}

	private static void syncCreate(FluentHtml add) {
		add.element = document.createElement(add.tag);
		if (add.attrs != null) {
			for (NameAttr name : add.attrs.keySet()) {
				add.element.setAttribute(name.nameValid(), add.attrs.get(name));
			}
		}
		if (add.styles != null) {
			for (NameStyle name : add.styles.keySet()) {
				add.element.getStyle().setProperty(name.nameValid(), add.styles.get(name));
			}
		}
		if (add.inner != null) {
			add.inner(add.inner);
		}
		if (add.listeners != null) {
			for (NameListener name : add.listeners.keySet()) {
				add.element.addEventListener(name.name(), add.listeners.get(name));
			}
		}
		if (add.childs != null) {
			for (FluentHtml child : add.childs) {
				while (child.tag == null) { // skip ReactC objects
					child = ((ReactC) child).generate();
				}
				syncCreate(child);
				add.element.appendChild(child.element);
			}
		}
	}

	private static boolean compare(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equals(str2));
	}

	// preconditions: we are attached (element!=null) and the other already has
	// elements
	private static void syncRender(FluentHtml add) {
		if (!compare(add.tag, add.element.getTagName())) {
			syncCreate(add);
			return;
		}
		// innerHtml
		if (!compare(add.inner, add.element.getInnerHTML())) {
			add.element.setInnerHTML(add.inner);
		}
		// Attrs
		// TODO: if add.attrs==null
		NamedNodeMap<Attr> elementAttrs = add.element.getAttributes();
		for (NameAttr name : add.attrs.keySet()) { // add or adjust
			String value = add.attrs.get(name);

			Attr elementNow = elementAttrs.getNamedItem(name.nameValid());
			if (elementNow == null) {
				add.element.setAttribute(name.nameValid(), value);
			} else if (!elementNow.getValue().equals(value)) {
				elementNow.setValue(value);
			}
		}
		for (int x = 0; x < elementAttrs.getLength(); x++) { // remove
			Attr elementAttr = elementAttrs.item(x);
			if (!add.attrs.containsKey(NameAttr.valueOfValid(elementAttr.getName()))) {
				elementAttr.delete();
			}
		}
		// Style
		CSSStyleDeclaration elementStyles = add.element.getStyle();
		// TODO: if add.styles==null
		for (NameStyle name : add.styles.keySet()) { // add or adjust
			String nameValid = name.nameValid();
			String value = add.styles.get(name);

			String elementNow = elementStyles.getPropertyValue(nameValid);
			if (elementNow == null) {
				elementStyles.setProperty(nameValid, value);
			} else if (!elementNow.equals(value)) {
				elementStyles.removeProperty(nameValid);
				// TODO is remove necessary?
				elementStyles.setProperty(nameValid, value);
			}
		}
		for (int x = 0; x < elementStyles.getLength(); x++) { // remove
			String elementStyle = elementStyles.item(x);
			if (!add.styles.containsKey(NameStyle.valueOfValid(elementStyle))) {
				elementStyles.removeProperty(elementStyle);
			}
		}
		// TODO remove all listeners!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		for (NameListener name : add.listeners.keySet()) { // add or adjust
			EventListener<?> value = add.listeners.get(name);
			// .............?
		}
		
		// TODO HIER GEBLEVEN private List<FluentHtml> childs;

		// TODO: functies toevoegen voor removeListener() e.d.
		System.out.println("I'm rerendering");
		// https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060#.wc4ze6sj1
	}

	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:
	// Constructor-tags:

	public Input input(String type, String name) {
		Input result = new Input(this);
		result.attr(NameAttr.type, type);
		result.attr(NameAttr.name, name);
		return result;
	}

	public Button button(String text) {
		Button result = new Button(this);
		result.inner(text);
		return result;
	}

	public Li li(String text) {
		Li result = new Li(this);
		result.inner(text);
		return result;
	}

	public Div div() {
		return new Div(this);
	}

	public Div div(List<? extends FluentHtml> list) {
		return (Div) div().add(list);
	}

	public Div div(FluentHtml[] list) {
		return (Div) div().add(list);
	}

	public Div divd(FluentHtml... fluentHtmls) {
		return div(fluentHtmls);
	}

	// UNORDERED YET

	public FluentHtml area() {
		return new FluentHtml("area", this);
	}

	public FluentHtml base() {
		return new FluentHtml("base", this);
	}

	public FluentHtml br() {
		return new FluentHtml("br", this);
	}

	public FluentHtml col() {
		return new FluentHtml("col", this);
	}

	public FluentHtml document() {
		return new FluentHtml("!DOCTYPE html", this);
	}

	public FluentHtml embed() {
		return new FluentHtml("embed", this);
	}

	public FluentHtml hr() {
		return new FluentHtml("hr", this);
	}

	public FluentHtml img() {
		return new FluentHtml("img", this);
	}

	public FluentHtml input() {
		return new FluentHtml("input", this);
	}

	public FluentHtml keygen() {
		return new FluentHtml("keygen", this);
	}

	public FluentHtml link() {
		return new FluentHtml("link", this);
	}

	public FluentHtml meta() {
		return new FluentHtml("meta", this);
	}

	public FluentHtml param() {
		return new FluentHtml("param", this);
	}

	public FluentHtml source() {
		return new FluentHtml("source", this);
	}

	public FluentHtml track() {
		return new FluentHtml("track", this);
	}

	public FluentHtml wbr() {
		return new FluentHtml("wbr", this);
	}

	public FluentHtml a(String text) {
		return new FluentHtml("a", this).inner(text);
	}

	public FluentHtml abbr() {
		return new FluentHtml("abbr", this);
	}

	public FluentHtml address() {
		return new FluentHtml("address", this);
	}

	public FluentHtml article() {
		return new FluentHtml("article", this);
	}

	public FluentHtml aside() {
		return new FluentHtml("aside", this);
	}

	public FluentHtml audio() {
		return new FluentHtml("audio", this);
	}

	public FluentHtml b(String text) {
		FluentHtml result = new FluentHtml("b", this);
		result.inner(text);
		return result;
	}

	public FluentHtml bdi(String text) {
		return new FluentHtml("bdi", this).inner(text);
	}

	public FluentHtml bdo() {
		return new FluentHtml("bdo", this);
	}

	public FluentHtml bdo(String text) {
		return new FluentHtml("bdo", this).inner(text);
	}

	public FluentHtml blockquote() {
		return new FluentHtml("blockquote", this);
	}

	public FluentHtml blockquote(String text) {
		return new FluentHtml("blockquote", this).inner(text);
	}

	public FluentHtml body() {
		return new FluentHtml("body", this);
	}

	public FluentHtml canvas() {
		return new FluentHtml("canvas", this);
	}

	public FluentHtml caption() {
		return new FluentHtml("caption", this);
	}

	public FluentHtml caption(String text) {
		return new FluentHtml("caption", this).inner(text);
	}

	public FluentHtml cite() {
		return new FluentHtml("cite", this);
	}

	public FluentHtml cite(String text) {
		return new FluentHtml("cite", this).inner(text);
	}

	public FluentHtml code() {
		return new FluentHtml("code", this);
	}

	public FluentHtml colgroup() {
		return new FluentHtml("colgroup", this);
	}

	public FluentHtml datalist() {
		return new FluentHtml("datalist", this);
	}

	public FluentHtml dd(String text) {
		return new FluentHtml("dd", this).inner(text);
	}

	public FluentHtml del(String text) {
		return new FluentHtml("del", this).inner(text);
	}

	public FluentHtml details() {
		return new FluentHtml("details", this);
	}

	public FluentHtml dfn(String text) {
		return new FluentHtml("dfn", this).inner(text);
	}

	public FluentHtml dialog() {
		return new FluentHtml("dialog", this);
	}

	public FluentHtml dialog(String text) {
		return new FluentHtml("dialog", this).inner(text);
	}

	public FluentHtml dl() {
		return new FluentHtml("dl", this);
	}

	public FluentHtml dt() {
		return new FluentHtml("dt", this);
	}

	public FluentHtml dt(String text) {
		return new FluentHtml("dt", this).inner(text);
	}

	public FluentHtml em() {
		return new FluentHtml("em", this);
	}

	public FluentHtml em(String text) {
		return new FluentHtml("em", this).inner(text);
	}

	public FluentHtml fieldset() {
		return new FluentHtml("fieldset", this);
	}

	public FluentHtml figcaption() {
		return new FluentHtml("figcaption", this);
	}

	public FluentHtml figcaption(String text) {
		return new FluentHtml("figcaption", this).inner(text);
	}

	public FluentHtml figure() {
		return new FluentHtml("figure", this);
	}

	public FluentHtml footer() {
		return new FluentHtml("footer", this);
	}

	public FluentHtml form() {
		return new FluentHtml("form", this);
	}

	public FluentHtml h1(String text) {
		return new FluentHtml("h1", this).inner(text);
	}

	public FluentHtml h2(String text) {
		return new FluentHtml("h2", this).inner(text);
	}

	public FluentHtml h3(String text) {
		return new FluentHtml("h3", this).inner(text);
	}

	public FluentHtml h4(String text) {
		return new FluentHtml("h4", this).inner(text);
	}

	public FluentHtml h5(String text) {
		return new FluentHtml("h5", this).inner(text);
	}

	public FluentHtml h6(String text) {
		return new FluentHtml("h6", this).inner(text);
	}

	public FluentHtml header() {
		return new FluentHtml("header", this);
	}

	public FluentHtml i() {
		return new FluentHtml("i", this);
	}

	public FluentHtml i(String text) {
		return new FluentHtml("i", this).inner(text);
	}

	public FluentHtml iframe() {
		return new FluentHtml("iframe", this);
	}

	public FluentHtml ins(String text) {
		return new FluentHtml("ins", this).inner(text);
	}

	public FluentHtml kbd() {
		return new FluentHtml("kbd", this);
	}

	public FluentHtml label(String text) {
		return new FluentHtml("label", this).inner(text);
	}

	public FluentHtml legend(String text) {
		return new FluentHtml("legend", this).inner(text);
	}

	public FluentHtml main() {
		return new FluentHtml("main", this);
	}

	public FluentHtml map() {
		return new FluentHtml("map", this);
	}

	public FluentHtml mark() {
		return new FluentHtml("mark", this);
	}

	public FluentHtml menu() {
		return new FluentHtml("menu", this);
	}

	public FluentHtml menuitem() {
		return new FluentHtml("menuitem", this);
	}

	public FluentHtml meter() {
		return new FluentHtml("meter", this);
	}

	public FluentHtml nav() {
		return new FluentHtml("nav", this);
	}

	public FluentHtml noscript() {
		return new FluentHtml("noscript", this);
	}

	public FluentHtml object() {
		return new FluentHtml("object", this);
	}

	public FluentHtml ol() {
		return new FluentHtml("ol", this);
	}

	public FluentHtml optgroup() {
		return new FluentHtml("optgroup", this);
	}

	public FluentHtml option(String text) {
		return new FluentHtml("option", this).inner(text);
	}

	public FluentHtml output() {
		return new FluentHtml("output", this);
	}

	public FluentHtml p() {
		return new FluentHtml("p", this);
	}

	public FluentHtml p(String text) {
		return new FluentHtml("p", this).inner(text);
	}

	public FluentHtml pre(String text) {
		return new FluentHtml("pre", this).inner(text);
	}

	public FluentHtml progress() {
		return new FluentHtml("progress", this);
	}

	public FluentHtml q(String text) {
		return new FluentHtml("q", this).inner(text);
	}

	public FluentHtml rp() {
		return new FluentHtml("rp", this);
	}

	public FluentHtml rt() {
		return new FluentHtml("rt", this);
	}

	public FluentHtml ruby() {
		return new FluentHtml("ruby", this);
	}

	public FluentHtml s(String text) {
		return new FluentHtml("s", this).inner(text);
	}

	public FluentHtml samp() {
		return new FluentHtml("samp", this);
	}

	public FluentHtml script() {
		return new FluentHtml("script", this);
	}

	public FluentHtml section() {
		return new FluentHtml("section", this);
	}

	public FluentHtml select() {
		return new FluentHtml("select", this);
	}

	public FluentHtml small() {
		return new FluentHtml("small", this);
	}

	public FluentHtml small(String text) {
		return new FluentHtml("small", this).inner(text);
	}

	public FluentHtml span() {
		return new FluentHtml("span", this);
	}

	public FluentHtml span(String text) {
		return new FluentHtml("span", this).inner(text);
	}

	public FluentHtml strong(String text) {
		return new FluentHtml("strong", this).inner(text);
	}

	public FluentHtml style() {
		return new FluentHtml("style", this);
	}

	public FluentHtml sub(String text) {
		return new FluentHtml("sub", this).inner(text);
	}

	public FluentHtml summary(String text) {
		return new FluentHtml("summary", this).inner(text);
	}

	public FluentHtml sup(String text) {
		return new FluentHtml("sup", this).inner(text);
	}

	public FluentHtml table() {
		return new FluentHtml("table", this);
	}

	public FluentHtml tbody() {
		return new FluentHtml("tbody", this);
	}

	public FluentHtml td() {
		return new FluentHtml("td", this);
	}

	public FluentHtml td(String text) {
		return new FluentHtml("td", this).inner(text);
	}

	public FluentHtml textarea() {
		return new FluentHtml("textarea", this);
	}

	public FluentHtml tfoot() {
		return new FluentHtml("tfoot", this);
	}

	public FluentHtml th() {
		return new FluentHtml("th", this);
	}

	public FluentHtml th(String text) {
		return new FluentHtml("th", this).inner(text);
	}

	public FluentHtml thead() {
		return new FluentHtml("thead", this);
	}

	public FluentHtml time() {
		return new FluentHtml("time", this);
	}

	public FluentHtml title() {
		return new FluentHtml("title", this);
	}

	public FluentHtml title(String text) {
		return new FluentHtml("title", this).inner(text);
	}

	public FluentHtml tr() {
		return new FluentHtml("tr", this);
	}

	public FluentHtml u() {
		return new FluentHtml("u", this);
	}

	public FluentHtml u(String text) {
		return new FluentHtml("u", this).inner(text);
	}

	public FluentHtml ul() {
		return new FluentHtml("ul", this);
	}

	public FluentHtml var() {
		return new FluentHtml("var", this);
	}

	public FluentHtml video() {
		return new FluentHtml("video", this);
	}

}

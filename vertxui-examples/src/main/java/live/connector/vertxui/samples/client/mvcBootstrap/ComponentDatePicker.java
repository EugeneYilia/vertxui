package live.connector.vertxui.samples.client.mvcBootstrap;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * An example to create a component. Note that the class View contains css and
 * scripts for library pikaday.js and moment.js.
 * 
 * @author ng
 *
 */
public class ComponentDatePicker extends Fluent {

	public static DateTimeFormat dateTimeFormat = new InnerDateTimeFormat("dd/MM/yyyy");

	private final String id = Math.random() + "";

	public static ArrayList<String> getCss() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdnjs.cloudflare.com/ajax/libs/pikaday/1.5.1/css/pikaday.min.css");
		return result;
	}

	public static ArrayList<String> getScripts() {
		ArrayList<String> result = new ArrayList<>();
		result.add("https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.17.1/moment.min.js");
		result.add("https://cdnjs.cloudflare.com/ajax/libs/pikaday/1.5.1/pikaday.min.js");
		return result;
	}

	private static class InnerDateTimeFormat extends DateTimeFormat {
		protected InnerDateTimeFormat(String pattern) {
			super(pattern, new DefaultDateTimeFormatInfo());
		}
	}

	public ComponentDatePicker() {
		super("INPUT", null);
		classs("form-control").att(Att.type, "text").id(id);

		// The non-component version was only:
		// Input("form-control", "text").id("datepicker");
	}

	@Override
	public void isRendered(boolean shown) {
		if (shown) {
			eval("new Pikaday({ field: document.getElementById('" + id + "'), format: 'DD/MM/YYYY' });");
		}
	}

	public Date getDate() {
		return dateTimeFormat.parse(domValue());
	}

}

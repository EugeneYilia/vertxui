package live.connector.vertxui.client;

import static live.connector.vertxui.client.test.Asserty.assertEquals;

import static live.connector.vertxui.client.fluent.Fluent.*;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.test.TestDOM;

public class FluentInnerRendering extends TestDOM {

	@Override
	public void tests() {
		inner();
		middleChildRemoval();
	}

	private void middleChildRemoval() {
		ViewOn<Integer> children = Fluent.body.add(0, i -> {
			switch (i) {
			case 0:
				return Ul(Li("a"), Li("b"), Li("c"));
			case 1:
				return Ul(Li("a"), Li("b"), Li("c"));
			default:
				return null;
			}
		});
		
		

	}

	private void inner() {
		String starttext = Math.random() + "aSeed";
		Fluent div = Fluent.body.div();

		assertEquals("0. real DOM is empty string before start", "", div.dom().getInnerText());

		div.in(starttext);

		assertEquals("1. given value match virtual DOM", starttext, div.in());
		assertEquals("1. given value match real DOM", starttext, div.dom().getInnerText());

		div.in(starttext); // manualtest: should skip

		div.in(null);
		assertEquals("2. given value match virtual DOM", null, div.in());
		assertEquals("2. given value match real DOM not null but empty string", "", div.dom().getInnerText());

		div.in(null); // manual test: should skip
	}

}

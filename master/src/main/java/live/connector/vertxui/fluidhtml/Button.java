package live.connector.vertxui.fluidhtml;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLButtonElement;

public class Button extends FluidHtml {

	protected Button(String text, FluidHtml element) {
		super("button", element);
		inner(text);
	}

	@Override
	public Button inner(String innerHtml) {
		return (Button) super.inner(innerHtml);
	}

	@Override
	public Button onClick(EventListener<MouseEvent> listener) {
		return (Button) super.onClick(listener);
	}

	@Override
	public Button id(String string) {
		return (Button) super.id(string);
	}

	public Button disable() {
		((HTMLButtonElement) element).setDisabled(true);
		return this;
	}

	public Button enable() {
		((HTMLButtonElement) element).setDisabled(false);
		return this;
	}

}

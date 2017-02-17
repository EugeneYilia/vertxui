package live.connector.vertxui.client.fluent;

/**
 * A base for all view elements, currently FluentBase for Fluent and ViewOnBase
 * for ViewOn and ViewOnBoth
 * 
 * @author Niels Gorisse
 *
 */
public interface Viewable {

	public int getCrc();

	public String getCrcString();

	public Viewable hide(boolean doit);

	public void isRendered(boolean state);

}

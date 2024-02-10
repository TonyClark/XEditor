package diagrams;

import console.Console;

public class PlotPanel extends DiagramPanel {
	public PlotPanel(String name,Console console, String svg) {
		super(name,console);
		setSVG(svg);
	}

}

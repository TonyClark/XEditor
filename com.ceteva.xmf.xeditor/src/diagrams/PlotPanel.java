package diagrams;

import console.Console;

public class PlotPanel extends DiagramPanel {
	public PlotPanel(Console console, String svg) {
		super(console);
		setSVG(svg);
	}

}

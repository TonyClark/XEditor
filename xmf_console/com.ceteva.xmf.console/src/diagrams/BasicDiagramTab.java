package diagrams;

import org.apache.batik.swing.svg.LinkActivationEvent;

import console.Console;

public class BasicDiagramTab extends DiagramTab {

	private SVGPanel     svgPanel = null;
	private Console      console  = null;
	private DiagramFrame frame    = null;
	private String       name;
	private String       diagram;

	public BasicDiagramTab(Console console, DiagramFrame frame, String name, String diagram) {
		this.console = console;
		this.frame = frame;
		this.diagram = diagram;
		this.name = name;
		this.svgPanel = new SVGPanel(console) {

			@Override
			public void linkClicked(LinkActivationEvent l) {
				// TODO Auto-generated method stub

			}
		};
		add(svgPanel);
		svgPanel.setSVG(diagram);
	}

}

package diagrams.model;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import console.Console;
import diagrams.DiagramFrame;
import diagrams.DiagramTab;
import diagrams.SVGPanel;
import web.SVG;

public class ModelDiagramTab extends DiagramTab {

	private SVGPanel     svgPanel = null;
	private Console      console  = null;
	private DiagramFrame frame    = null;
	private Model        model;

	public ModelDiagramTab(String name,Console console, DiagramFrame frame, Model model) {
		this.console  = console;
		this.frame    = frame;
		this.model    = model;
		this.svgPanel = new ModelDiagramPanel(name,console,model,frame);
		setLayout(new BorderLayout());
		add(new JScrollPane(svgPanel), BorderLayout.CENTER);
	}

}

package diagrams;

import java.awt.BorderLayout;

import console.Console;

public class PlotTab extends DiagramTab {

	private Console console;
	private String name;

	public PlotTab(Console console,String name,String svg) {
		this.console = console;
		this.name = name;
		PlotPanel panel = new PlotPanel(name,console,svg);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
	}

}

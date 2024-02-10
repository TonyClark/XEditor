package diagrams.model;

import java.util.Vector;

import console.Console;

public class Model {

	private Package               _package;
	private EdgeStyle             edgeStyle = EdgeStyle.ORTHO;
	private String                name;
	private Vector<ModelMenuItem> menuItems = new Vector<ModelMenuItem>();

	public Model(String owner, String name) {
		_package  = new Package(owner, name);
		this.name = name;
	}

	public void addMenuItem(ModelMenuItem item) {
		menuItems.add(item);
	}

	public void createMenu(String name, Vector<String> names) {
		if (names.isEmpty())
			addMenuItem(new ModelMenuItem(name));
		else {
			ModelMenu menu = new ModelMenu(name, new Vector<ModelMenuItem>());
			for (String n : names) {
				menu.add(new ModelMenuItem(n));
			}
			addMenuItem(menu);
		}
	}

	public EdgeStyle getEdgeStyle() {
		return edgeStyle;
	}

	public Vector<ModelMenuItem> getMenuItems() {
		return menuItems;
	}

	public Package getPackage() {
		return _package;
	}

	public String getPlantUML() {
		String plantUML = "@startuml\n";
		plantUML += "skinparam linetype " + edgeStyle.name().toLowerCase() + "\n";
		plantUML += "<style>\nnote {\nFontsize 11\nFontcolor blue\nMaximumWidth 150\n}\n</style>\n";
		plantUML += _package.getPlantUML();
		return plantUML + "@enduml\n";
	}

	public void setEdgeStyle(EdgeStyle edgeStyle) {
		this.edgeStyle = edgeStyle;
	}

	public void showImports(String path, boolean show) {
		_package.showImports(path, show);
	}

	public void menuEvent(String menuName, ModelDiagramPanel panel) {
		Vector<String> names = new Vector<String>();
		names.add(menuName);
		Console.CONSOLE.call("diagramMenuEvent", new Object[] { name, names }, (model) -> {
			panel.setModel((Model) model);
			panel.regenerate();
		});
	}

	public String getName() {
		return name;
	}

}

package diagrams.model;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Model {

	private Package _package;
	private EdgeStyle edgeStyle = EdgeStyle.ORTHO;

	public Model(String owner, String name) {
		_package = new Package(owner, name);
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

	public void showImports(String path, boolean show) {
		_package.showImports(path, show);
	}

	public EdgeStyle getEdgeStyle() {
		return edgeStyle;
	}

	public void setEdgeStyle(EdgeStyle edgeStyle) {
		this.edgeStyle = edgeStyle;
	}

}

package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public abstract class PackageElement implements Visible {

	private String  name;
	private boolean visible = true;

	public PackageElement(String name) {
		this.name  = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public abstract String getPlantUML();

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public abstract void populate(VisibilityTree tree, DefaultMutableTreeNode node, Vector<TreePath> visible, Vector<TreePath> invisible);

}

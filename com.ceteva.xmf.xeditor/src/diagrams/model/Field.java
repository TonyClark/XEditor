package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Field implements Visible {

	private String  name;
	private Type    type;
	private boolean visible = true;

	public Field(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public String getPlantUML() {
		if (isVisible())
			return "<size:12><color:#DarkGreen>" + name + ":<color:#Blue>" + type.getPlantUML() + "\n";
		else
			return "";
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else invisible.add(new TreePath(child.getPath()));
	}

	public String toString() {
		return name + ":" + type;
	}

}

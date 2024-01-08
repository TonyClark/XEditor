package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Slot implements Visible {

	private String  name;
	private Object  value;
	private boolean visible = true;

	public Slot(String name, Object value) {
		super();
		this.name  = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean isVisible) {
		visible = isVisible;
	}

	public String getPlantUML() {
		if (isVisible())
			return "<size:12><color:#DarkGreen>" + name + " = <color:#Blue>" + value + "\n";
		else
			return "";
	}

	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else invisible.add(new TreePath(child.getPath()));
	}

}

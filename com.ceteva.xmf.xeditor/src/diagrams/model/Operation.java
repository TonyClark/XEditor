package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Operation implements Visible {

	private String      name;
	private Vector<Arg> args     = new Vector<Arg>();
	private Type        type;
	private boolean     visible  = false;

	public Operation(String name, Vector<Arg> args,Type type) {
		super();
		this.name = name;
		this.args = args;
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void addArg(String name, Type type) {
		args.add(new Arg(name, type));
	}

	public String getPlantUML() {
		if (isVisible()) {
			String s = "<size:12><color:#DarkGreen>" + name + "(";
			for (int i = 0; i < args.size(); i++) {
				s += args.get(i).getPlantUML();
				if (i + 1 < args.size()) {
					s += ",";
				}
			}
			return s + "):" + type.getPlantUML() + "\n";
		} else
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
		String s =  name + "(";
		for (int i = 0; i < args.size(); i++) {
			s += args.get(i).toString();
			if (i + 1 < args.size()) {
				s += ",";
			}
		}
		return s + "):" + type.toString() + "\n";
	}

}

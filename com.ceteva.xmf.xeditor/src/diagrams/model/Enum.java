package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Enum extends PackageElement {

	private Vector<String> names;

	public Enum(String name,Vector<String> names) {
		super(name);
		this.names = names;
	}

	@Override
	public String getPlantUML() {
		if (isVisible()) {
			String s = "enum \"<size:14><color:#Blue>" + getName() + "\" as " + getName() + " {\n";
			for(String name : names) {
				s += name + "\n";
			}
			return s + "}\n";
		} else
			return "";
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		tree.getCheckingModel().addCheckingPath(new TreePath(child.getPath()));
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}

}

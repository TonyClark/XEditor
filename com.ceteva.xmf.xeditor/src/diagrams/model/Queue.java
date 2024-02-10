package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Queue extends PackageElement {

	public Queue(String name) {
		super(name);
	}

	@Override
	public String getPlantUML() {
		return "queue " + getName() + " {\n}\n";
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else invisible.add(new TreePath(child.getPath()));
	}

}

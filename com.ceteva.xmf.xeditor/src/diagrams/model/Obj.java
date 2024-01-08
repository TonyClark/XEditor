package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Obj extends PackageElement {

	private Vector<Slot> slots = new Vector<Slot>();

	public Obj(String type) {
		super(type);
	}

	public void addSlot(String name, Object value) {
		slots.add(new Slot(name, value));
	}

	@Override
	public String getPlantUML() {
		if (isVisible()) {
			String s = "map \"<size:14><color:#Green>" + getName() + "\" as " + getName() + " {\n";
			if (!allSlotsHidden()) {
				s += "-- <size:12> <color:#DarkGreen> --\n";
				for (Slot slot : slots) {
					s += slot.getName() + " => " + slot.getValue() + "\n";
				}
			}
			return s + "}\n";
		} else
			return "";
	}

	private boolean allSlotsHidden() {
		for (Slot s : slots) {
			if (s.isVisible())
				return false;
		}
		return true;
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		tree.getCheckingModel().addCheckingPath(new TreePath(child.getPath()));
		for (Slot s : slots) {
			s.populate(tree, child, visible, invisible);
		}
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}
}
package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Obj extends PackageElement {

	private Vector<Slot> slots      = new Vector<Slot>();
	private String       type;
	private String       background = "white";

	public Obj(String id, String type) {
		super(id);
		this.type = type;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getType() {
		return type;
	}

	public Slot addSlot(String name, Object value) {
		Slot slot = new Slot(name, value);
		slots.add(slot);
		return slot;
	}

	@Override
	public String getPlantUML() {
		if (isVisible()) {
			String label = getName() + ":" + getType();
			String s = "object \"<size:14>" + label + "\" as " + getName() + " " + "#" + background + " {\n";
			if (!allSlotsHidden()) {
				s += "-- <size:12> <color:#DarkGreen> --\n";
				for (Slot slot : slots) {
					s += slot.getPlantUML();
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
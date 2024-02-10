package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Table extends PackageElement {

	private String       id;
	private Vector<Slot> slots = new Vector<Slot>();

	public Table(String name) {
		super(name);
		this.id = name;
	}

	public void addSlot(String name, Object value) {
		slots.add(new Slot(name, value));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getPlantUML() {
		String s = "map \"" + getName() + "\" as " + id + "{\n";
		for (Slot slot : slots) {
			s += "  " + slot.getName() + " => " + slot.getValue() + "\n";
		}
		return s + "}\n";
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode node, Vector<TreePath> visible, Vector<TreePath> invisible) {
		// TODO Auto-generated method stub

	}

}

package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Class extends PackageElement {

	private Vector<Field>     fields     = new Vector<Field>();
	private Vector<Operation> operations = new Vector<Operation>();

	public Class(String name) {
		super(name);
	}

	public void addField(String name, Type type) {
		fields.add(new Field(name, type));
	}

	public Operation addOperation(String name, Vector<Arg> args, Type type) {
		Operation op = new Operation(name, args, type);
		operations.add(op);
		return op;
	}

	@Override
	public String getPlantUML() {
		if (isVisible()) {
			String s = "class \"<size:14><color:#Blue>" + getName() + "\" as " + getName() + " {\n";
			if (!allFieldsHidden()) {
				s += "-- <size:12> <color:#DarkGreen> Attributes --\n";
				for (Field f : fields) {
					s += f.getPlantUML();
				}
			}
			if (!allOperationsHidden()) {
				s += "-- <size:12> <color:#DarkGreen> Operations --\n";
				for (Operation o : operations) {
					s += o.getPlantUML();
				}
			}
			return s + "}\n";
		} else
			return "";
	}

	private boolean allFieldsHidden() {
		for (Field f : fields) {
			if (f.isVisible())
				return false;
		}
		return true;
	}

	private boolean allOperationsHidden() {
		for (Operation o : operations) {
			if (o.isVisible())
				return false;
		}
		return true;
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		tree.getCheckingModel().addCheckingPath(new TreePath(child.getPath()));
		for (Field f : fields) {
			f.populate(tree, child, visible, invisible);
		}
		for (Operation o : operations) {
			o.populate(tree, child, visible, invisible);
		}
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}

}

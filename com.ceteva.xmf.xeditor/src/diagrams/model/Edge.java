package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Edge implements Visible {

	private PackageElement source;
	private PackageElement target;
	private String         label;
	private boolean        visible = true;
	private EdgeType       type    = EdgeType.DIRECTED;

	public Edge(PackageElement source, PackageElement target, String label) {
		super();
		this.source = source;
		this.target = target;
		this.label  = label;
	}

	public boolean isVisible() {
		return visible;
	}

	public EdgeType getType() {
		return type;
	}

	public void setType(EdgeType type) {
		this.type = type;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getPlantUML() {
		if (isVisible() && source.isVisible() && target.isVisible()) {
			switch (type) {
			case DIRECTED:
				return source.getName() + " --> " + virtualLabel() + " " + target.getName() + "\n";
			case INHERITS:
				return source.getName() + " -u-|> " + virtualLabel() + " " + target.getName() + "\n";
			default:
				throw new Error("unknown type of edge: " + type);
			}
		} else
			return "";
	}

	public String virtualLabel() {
		if (label.equals(""))
			return "";
		else
			return "\"" + label + "\"";
	}

	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else invisible.add(new TreePath(child.getPath()));
	}

	public String toString() {
		return label + ": " + source.getName() + " -> " + target.getName();
	}

	public PackageElement getSource() {
		return source;
	}

	public PackageElement getTarget() {
		return target;
	}

	public String getLabel() {
		return label;
	}

}

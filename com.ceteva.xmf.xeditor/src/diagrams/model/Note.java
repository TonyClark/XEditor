package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Note implements Visible {

	private PackageElement target;
	private String         note;
	private boolean        isVisible = true;

	public Note(PackageElement target, String note) {
		super();
		this.target = target;
		this.note   = note.trim().replace("\n", " ");
	}

	public String getPlantUML() {
		if (isVisible())
			return "note top of " + target.getName() + "\n" + note + "\nend note\n";
		else
			return "";
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}

	public String toString() {
		return "Note(" + target.getName() + "," + note.substring(0, Math.min(note.length(), 20)) + ")";
	}

}

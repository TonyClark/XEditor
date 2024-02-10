package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ImportedClass extends PackageElement {

	public static String constructName(String sourcePath, String name, String targetPath) {
		return sourcePath.replace(':', '_') + "_" + name + "_into_" + targetPath.replace(':', '_');
	}

	private String sourcePath;
	private String className;

	public ImportedClass(String sourcePath, String targetPath, String name) {
		super(constructName(sourcePath, name, targetPath));
		this.sourcePath = sourcePath;
		this.className  = name;
		if (sourcePath.endsWith("XCore"))
			setVisible(false);
	}

	@Override
	public String getPlantUML() {
		if (isVisible())
			return "class \"<size:14><color:#Blue>" + className + "\" as " + getName() + " {\n <size:8><color:#Red> from " + sourcePath + "\n}\n";
		else
			return "";
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getClassName() {
		return className;
	}

	public String toString() {
		return className + " from " + sourcePath;
	}

}

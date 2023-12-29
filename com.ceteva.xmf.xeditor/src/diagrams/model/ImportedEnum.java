package diagrams.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ImportedEnum extends ImportedClass {

	public ImportedEnum(String sourcePath, String path, String name) {
		super(sourcePath,path,name);
	}

	@Override
	public String getPlantUML() {
		if (isVisible())
			return "enum \"<size:14><color:#Blue>" + getClassName() + "\" as " + getName() + " {\n <size:8><color:#Red> from " + getSourcePath() + "\n}\n";
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

}

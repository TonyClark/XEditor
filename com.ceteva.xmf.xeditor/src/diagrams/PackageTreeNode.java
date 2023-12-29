package diagrams;

import javax.swing.tree.DefaultMutableTreeNode;

public class PackageTreeNode extends DefaultMutableTreeNode {

	public PackageTreeNode(String path) {
		super(path);
	}

	public String toString() {
		String[] path = getUserObject().toString().split("::");
		return path[path.length - 1];
	}

}

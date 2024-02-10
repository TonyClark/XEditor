package diagrams.model;

import javax.swing.tree.DefaultMutableTreeNode;

public class ImportNode extends DefaultMutableTreeNode {

	private String path;

	public ImportNode(String path) {
		super("Import(" + path + ")");
		this.path = path;
	}

	public String getImportedPath() {
		return path;
	}

}

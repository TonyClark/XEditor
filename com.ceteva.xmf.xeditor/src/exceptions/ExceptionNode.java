package exceptions;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import filtertree.ImageProvider;

public class ExceptionNode implements ImageProvider{

	private String                label;
	private Vector<ExceptionNode> children = new Vector<ExceptionNode>();

	public ExceptionNode(String label, Vector<ExceptionNode> children) {
		this.label    = label;
		this.children = children;
	}

	public void createTree(ExceptionTree tree, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
		tree.add(node,parent);
		for(ExceptionNode child : children) {
			child.createTree(tree, node);
		}

	}

	public void createTree(ExceptionTree tree) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
		tree.setModel(new DefaultTreeModel(node));
		for(ExceptionNode child : children) {
			child.createTree(tree, node);
		}
	}

	public void dispose() {
		for(ExceptionNode child : children) {
			child.dispose();
		}
	}
	
	public String toString() {
		return label;
	}

	@Override
	public String getImageFile() {
		return "icons/warning.png";
	}

	@Override
	public void setImageFile(String imageFile) {

	}

	public void doubleClick(int x, int y) {
	}

}

package filtertree;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class JTreeUtil {
	
	public static void setTreeExpandedState(JTree tree, boolean expanded) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		setNodeExpandedState(tree, node, expanded);
	}

	public static void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
		for (DefaultMutableTreeNode treeNode : children(node)) {
			setNodeExpandedState(tree, treeNode, expanded);
		}
		if (!expanded && node.isRoot()) {
			return;
		}
		TreePath path = new TreePath(node.getPath());
		if (expanded) {
			tree.expandPath(path);
		} else {
			tree.collapsePath(path);
		}
	}

	public static DefaultMutableTreeNode copyNode(DefaultMutableTreeNode oldNode) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(oldNode.getUserObject());
		for (DefaultMutableTreeNode oldChildNode : JTreeUtil.children(oldNode)) {
			DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(oldChildNode.getUserObject());
			newNode.add(newChildNode);
			if (!oldChildNode.isLeaf()) {
				copyChildrenTo(oldChildNode, newChildNode);
			}
		}
		return newNode;
	}

	public static void copyChildrenTo(DefaultMutableTreeNode from, DefaultMutableTreeNode to) {
		for (DefaultMutableTreeNode oldChildNode : JTreeUtil.children(from)) {
			DefaultMutableTreeNode newChildNode = new DefaultMutableTreeNode(oldChildNode.getUserObject());
			to.add(newChildNode);
			if (!oldChildNode.isLeaf()) {
				copyChildrenTo(oldChildNode, newChildNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<DefaultMutableTreeNode> children(DefaultMutableTreeNode node) {
		Vector<DefaultMutableTreeNode> list = new Vector<DefaultMutableTreeNode>();
		for (int i = 0; i < node.getChildCount(); i++) {
			list.add((DefaultMutableTreeNode) node.getChildAt(i));
		}
		return list;
	}
}
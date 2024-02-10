package diagrams.model;

import java.awt.Font;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;

public class VisibilityTree extends CheckboxTree {

	private static DefaultMutableTreeNode root;
	public static Font                    font = new Font("Ariel", Font.PLAIN, 10);

	public void add(DefaultMutableTreeNode child, DefaultMutableTreeNode parent) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		String childLabel = child.toString();
		int i = 0;
		while (i < parent.getChildCount() && childLabel.compareTo(parent.getChildAt(i).toString()) > 0)
			i++;
		model.insertNodeInto(child, parent, i);
		setCellRenderer(new VisibilityTreeCellRenderer(font.getSize(),font));
	}

	public VisibilityTree(ModelDiagramPanel panel) {
		super();
		setFont(font);
		root = new DefaultMutableTreeNode(panel.getModel().getPackage());
		DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
		treeModel.setRoot(root);
		Vector<TreePath> visible = new Vector<TreePath>();
		Vector<TreePath> invisible = new Vector<TreePath>();

		for (String i : panel.getModel().getPackage().getImports()) {
			add(new ImportNode(i), root);
		}

		for (PackageElement e : panel.getModel().getPackage().getContents()) {
			e.populate(this, root, visible, invisible);
		}
		for (Edge e : panel.getModel().getPackage().getEdges()) {
			e.populate(this, root, visible, invisible);
		}
		for (Note n : panel.getModel().getPackage().getNotes()) {
			n.populate(this, root, visible, invisible);
		}

		TreePath[] visiblePaths = visible.toArray(new TreePath[0]);
		TreePath[] invisiblePaths = invisible.toArray(new TreePath[0]);
		getCheckingModel().addCheckingPaths(visiblePaths);
		getCheckingModel().removeCheckingPaths(invisiblePaths);

		addTreeCheckingListener(new TreeCheckingListener() {
			public void valueChanged(TreeCheckingEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				Object o = node.getUserObject();
				if (o instanceof Visible) {
					Visible s = (Visible) o;
					s.setVisible(e.isCheckedPath());
					panel.regenerate();
				} else if (node instanceof ImportNode) {
					ImportNode importNode = (ImportNode) node;
					if (e.isCheckedPath())
						panel.showImports(importNode.getImportedPath(), true);
					else
						panel.showImports(importNode.getImportedPath(), false);
					panel.regenerate();
				}
			}
		});
		setExpandsSelectedPaths(true);
	}

}

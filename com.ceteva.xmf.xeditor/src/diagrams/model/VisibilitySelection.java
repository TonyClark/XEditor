package diagrams.model;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import console.Console;

public class VisibilitySelection extends JFrame {

	private VisibilityTree    tree;
	private ModelDiagramPanel panel;

	public VisibilitySelection(Console console, ModelDiagramPanel panel) {
		super();
		this.panel = panel;
		setLayout(new BorderLayout());
		tree = new VisibilityTree(panel);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(new VisibilityControls(this), BorderLayout.SOUTH);
	}

	private void done() {
		setVisible(false);
	}

	public void toggleAttributes(boolean selected) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Vector<TreePath> selectedPaths = new Vector<TreePath>();
		Vector<TreePath> deselectedPaths = new Vector<TreePath>();
		toggleAttributes(node, selected, selectedPaths, deselectedPaths);
		tree.getCheckingModel().addCheckingPaths(selectedPaths.toArray(new TreePath[0]));
		tree.getCheckingModel().removeCheckingPaths(deselectedPaths.toArray(new TreePath[0]));
		panel.regenerate();
	}

	private void toggleAttributes(DefaultMutableTreeNode node, boolean selected, Vector<TreePath> selectedPaths, Vector<TreePath> deselectedPaths) {
		if (node.getUserObject() instanceof Field) {
			Field field = (Field) node.getUserObject();
			TreePath path = new TreePath(node.getPath());
			field.setVisible(selected);
			if (selected) {
				selectedPaths.add(path);
			} else
				deselectedPaths.add(path);
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				toggleAttributes((DefaultMutableTreeNode) node.getChildAt(i), selected, selectedPaths, deselectedPaths);
			}
		}
	}

	public void toggleClasses(boolean selected) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Vector<TreePath> selectedPaths = new Vector<TreePath>();
		Vector<TreePath> deselectedPaths = new Vector<TreePath>();
		toggleClasses(node, selected, selectedPaths, deselectedPaths);
		panel.regenerate();
	}

	private void toggleClasses(DefaultMutableTreeNode node, boolean selected, Vector<TreePath> selectedPaths, Vector<TreePath> deselectedPaths) {
		if (node.getUserObject() instanceof Class) {
			Class c = (Class) node.getUserObject();
			TreePath path = new TreePath(node.getPath());
			c.setVisible(selected);
			if (selected) {
				selectedPaths.add(path);
			} else
				deselectedPaths.add(path);
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				toggleClasses((DefaultMutableTreeNode) node.getChildAt(i), selected, selectedPaths, deselectedPaths);
			}
		}
	}

	public void togglePackages(boolean selected) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Vector<TreePath> selectedPaths = new Vector<TreePath>();
		Vector<TreePath> deselectedPaths = new Vector<TreePath>();
		togglePackages(node, selected, selectedPaths, deselectedPaths);
		panel.regenerate();
	}

	private void togglePackages(DefaultMutableTreeNode node, boolean selected, Vector<TreePath> selectedPaths, Vector<TreePath> deselectedPaths) {
		if (node.getUserObject() instanceof Package) {
			Package p = (Package) node.getUserObject();
			TreePath path = new TreePath(node.getPath());
			p.setVisible(selected);
			if (selected) {
				selectedPaths.add(path);
			} else
				deselectedPaths.add(path);
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				togglePackages((DefaultMutableTreeNode) node.getChildAt(i), selected, selectedPaths, deselectedPaths);
			}
		}
	}

	public void toggleOperations(boolean selected) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Vector<TreePath> selectedPaths = new Vector<TreePath>();
		Vector<TreePath> deselectedPaths = new Vector<TreePath>();
		toggleOperations(node, selected, selectedPaths, deselectedPaths);
		panel.regenerate();
	}

	private void toggleOperations(DefaultMutableTreeNode node, boolean selected, Vector<TreePath> selectedPaths, Vector<TreePath> deselectedPaths) {
		if (node.getUserObject() instanceof Operation) {
			Operation o = (Operation) node.getUserObject();
			TreePath path = new TreePath(node.getPath());
			o.setVisible(selected);
			if (selected) {
				selectedPaths.add(path);
			} else
				deselectedPaths.add(path);
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				toggleOperations((DefaultMutableTreeNode) node.getChildAt(i), selected, selectedPaths, deselectedPaths);
			}
		}
	}

}

package console.browser;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import console.Console;
import filtertree.HoverProvider;
import filtertree.TradingProjectTreeRenderer;
import repl.XMFREPL;

public class ElementBrowser extends JTree implements MouseListener {

	private Console console;
	private int     refreshed = -1;

	public ElementBrowser(Console console) {
		this.console = console;
		addMouseListener(this);
		setFont(new Font("Arial", Font.PLAIN, 10));
		setCellRenderer(new TradingProjectTreeRenderer());
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public void addChild(int parent, int child, String label) {
		// Maintain in label order...
		ElementNode      parentNode = getNode((ElementNode) getModel().getRoot(), parent);
		ElementNode      childNode  = new ElementNode(child, label);
		DefaultTreeModel model      = (DefaultTreeModel) getModel();
		int              index      = -1;
		for (int i = 0; i < parentNode.getChildCount() && index == -1; i++) {
			ElementNode n = (ElementNode) parentNode.getChildAt(i);
			if (n.getLabel().compareTo(label) > 0)
				index = i;
		}
		if (index == -1)
			model.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
		else
			model.insertNodeInto(childNode, parentNode, index);
	}

	public void addChild(ElementNode parentNode, ElementNode childNode) {
		// Maintain in label order...
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		int              index = -1;
		for (int i = 0; i < parentNode.getChildCount() && index == -1; i++) {
			ElementNode n = (ElementNode) parentNode.getChildAt(i);
			if (n.getLabel().compareTo(childNode.getLabel()) > 0)
				index = i;
		}
		if (index == -1)
			model.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
		else
			model.insertNodeInto(childNode, parentNode, index);
	}

	private ElementNode getNode(ElementNode node, int elementId) {
		if (node.getElementId() == elementId)
			return node;
		else {
			for (int i = 0; i < node.getChildCount(); i++) {
				ElementNode n = getNode((ElementNode) node.getChildAt(i), elementId);
				if (n != null)
					return n;
			}
			return null;
		}
	}

	public ElementNode setRoot(Vector<Object> tree) {
		ElementNode node = createTree(tree);
		setModel(new DefaultTreeModel(node));
		return node;
	}

	private ElementNode createTree(Vector<Object> tree) {
		String      label  = (String) tree.get(0);
		int         handle = (int) tree.get(1);
		ElementNode node   = new ElementNode(handle, label);
		for (int i = 2; i < tree.size(); i++) {
			ElementNode child = createTree((Vector<Object>) tree.get(i));
			addChild(node, child);
		}
		return node;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	private void popUp(MouseEvent event) {
		if (event.isPopupTrigger()) {
			TreePath path = getSelectionPath();
			if (path != null) {
				ElementNode node = (ElementNode) path.getLastPathComponent();
				int         id   = node.getElementId();
				Console.call("getBrowserMenu", new Object[] { id }, (results) -> {
					Vector<MItem> items = (Vector<MItem>) results;
					if (items.size() > 0) {
						JPopupMenu popup = new JPopupMenu();
						for (MItem item : items) {
							item.populate(popup, id);
						}
						popup.show(this, event.getX(), event.getY());
					}
				});
			}
		}
	}

	public void refresh(int id) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		ElementNode      root  = (ElementNode) model.getRoot();
		ElementNode      node  = getNode(root, id);
		if (node == root) {
			Console.send("refreshBrowserRoot", new Object[] {});
		} else {
			if (refreshed != id) {
				refreshed = id;
				Vector<MutableTreeNode> children = new Vector<MutableTreeNode>();
				for (int i = 0; i < node.getChildCount(); i++) {
					children.add((MutableTreeNode) node.getChildAt(i));
				}
				for (MutableTreeNode child : children) {
					model.removeNodeFromParent(child);
				}
				Console.send("refreshBrowserElement", new Object[] { node.getElementId() });
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (event.isPopupTrigger()) {
			popUp(event);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void setElementImage(int id, String imageFile) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		ElementNode      root  = (ElementNode) model.getRoot();
		ElementNode      node  = getNode(root, id);
		if (node != null) {
			node.setImageFile(imageFile);
			repaint();
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point p      = event.getPoint();
		int   selRow = getRowForLocation(p.x, p.y);
		if (selRow != -1) {
			TreePath    path = getPathForRow(selRow);
			ElementNode node = (ElementNode) path.getLastPathComponent();
			if (node instanceof HoverProvider) {
				HoverProvider h = (HoverProvider) node;
				return h.getText();
			} else
				return null;
		} else
			return null;
	}

	public void setHoverText(int id, String text) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		ElementNode      root  = (ElementNode) model.getRoot();
		ElementNode      node  = getNode(root, id);
		text = "<html>" + text.replaceAll("\n", "<br>") + "</html>";
		if (node != null) {
			node.setText(text);
			repaint();
		}
	}
}

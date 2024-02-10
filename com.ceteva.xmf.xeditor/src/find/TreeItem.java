package find;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class TreeItem extends DefaultMutableTreeNode {

	private String   marker = ""; // Describes the icon on the tree node.
	private String   id     = ""; // Id to return to XMF when activated.
	private String   label  = ""; // Display this in Finder.
	private int      handler;     // XMF object to handle selection actions.
	private ItemMenu menu;        // Menu in the form of [LABEL,MENU...] the selected label is given to handler.

	public TreeItem(String label) {
		super(label);
	}

	public TreeItem(String marker, String id, String label, int handler, ItemMenu menu) {
		super(label);
		this.marker = marker;
		this.id = id;
		this.label = label;
		this.handler = handler;
		this.menu = menu;
	}

	public String getMarker() {
		return marker;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	public int getHandler() {
		return handler;
	}

	public ItemMenu getMenu() {
		return menu;
	}

	public void select(Finder finder) {
		TreeNode[]     path   = getPath();
		Vector<String> idPath = new Vector<String>();
		for (int i = 1; i < path.length; i++) {
			TreeItem item = (TreeItem) path[i];
			idPath.add(item.getId());
		}
		finder.send(handler, "select", idPath);
	}

	public String fullLabel() {
		TreeNode[] path  = getPath();
		String     label = "";
		for (int i = 1; i < path.length; i++) {
			TreeItem item = (TreeItem) path[i];
			if (label == "")
				label = item.getLabel();
			else
				label = label + "::" + item.getLabel();
		}
		return label;
	}

	public boolean contains(TreeItem item) {
		if (this == item)
			return true;
		else {
			for (int i = 0; i < getChildCount(); i++) {
				TreeItem child = (TreeItem) getChildAt(i);
				if (child.contains(item))
					return true;
			}
			return false;
		}
	}

}

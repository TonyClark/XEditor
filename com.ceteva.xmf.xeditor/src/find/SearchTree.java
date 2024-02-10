package find;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class SearchTree extends JPanel implements MouseListener {

	private Finder          finder;
	private TreeItem        root  = new TreeItem("");
	private JTree           tree  = new JTree(root);
	private Stack<TreeItem> stack = new Stack<TreeItem>();
	JLabel                  label = new JLabel("---Items---");

	public SearchTree(Finder finder) {
		this.finder = finder;
		setLayout(new BorderLayout());
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.setShowsRootHandles(false);
		tree.addMouseListener(this);
		tree.setFont(Finder.FONT);
		label.setFont(Finder.FONT);
		setBorder(BorderFactory.createRaisedBevelBorder());
		stack.push(root);
	}

	public void openEntry(String marker, String id, String label, int doubleClick, ItemMenu menu) {
		TreeItem item = new TreeItem(marker, id, label, doubleClick, menu);
		stack.peek().add(item);
		stack.push(item);
	}

	public void closeEntry() {
		TreeItem item = stack.pop();
		sort(item);
	}

	private void sort(TreeItem item) {
		ArrayList<TreeNode> children = Collections.list(item.children());
		Collections.sort(children, (n1, n2) -> compare((TreeItem) n1, (TreeItem) n2));
		item.removeAllChildren();
		Iterator<TreeNode> childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			item.add((DefaultMutableTreeNode) childrenIterator.next());
		}
	}

	private int compare(TreeItem n1, TreeItem n2) {
		return n1.getLabel().compareTo(n2.getLabel());
	}

	public Vector<TreeItem> search(String query, boolean isRegExp) {
		Vector<TreeItem> results = new Vector<TreeItem>();
		search(query, root, results, isRegExp);
		return results;
	}

	private void search(String query, TreeItem current, Vector<TreeItem> results, boolean isRegExp) {
		if (!isRegExp && current.getLabel().startsWith(query)) {
			results.add(current);
		}
		if (isRegExp && current.getLabel().matches(query)) {
			results.add(current);
		}
		for (int i = 0; i < current.getChildCount(); i++) {
			search(query, (TreeItem) current.getChildAt(i), results, isRegExp);
		}
	}

	public void select(Vector<TreeItem> items) {
		TreePath[] paths = new TreePath[items.size()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = new TreePath(items.get(i).getPath());
		}
		collapseAll();
		tree.setSelectionPaths(paths);
	}

	public void collapseAll() {
		for (int i = tree.getRowCount() - 1; i >= 0; i--) {
			tree.collapseRow(i);
		}
	}

	public void expandAll() {
		for (int i = tree.getRowCount() - 1; i >= 0; i--) {
			tree.expandRow(i);
		}
	}

	public void collapsePath() {
		for (int i = 0; i < root.getChildCount(); i++) {
			tree.collapsePath(new TreePath(root.getChildAt(i)));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			doubleClick();
		}
	}

	private void doubleClick() {
		TreePath[] selected = tree.getSelectionPaths();
		for (TreePath path : selected) {
			TreeItem item = (TreeItem) path.getLastPathComponent();
			finder.addSearchResult(item);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

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

}

package diagrams;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class PackageDialog extends JDialog implements MouseListener {

	private final JPanel contentPanel = new JPanel();
	private JTree        tree         = null;
	private String       path         = null;

	public PackageDialog(Vector<Object> packages) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Package Dialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		populateTree(root, packages, "");
		tree = new JTree(root);
		setNodeExpandedState(tree, root, true);
		contentPanel.add(new JScrollPane(tree));
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectNode();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PackageDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		tree.addMouseListener(this);
	}

	protected void selectNode() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null)
			return;
		Object userObject = node.getUserObject();
		path = userObject.toString();
		PackageDialog.this.dispose();
	}

	private static int sortItems(Object i1, Object i2) {
		Vector<Object> v1    = (Vector<Object>) i1;
		Vector<Object> v2    = (Vector<Object>) i2;
		String         name1 = (String) v1.get(0);
		String         name2 = (String) v2.get(0);
		return name1.compareTo(name2);
	}

	private void populateTree(DefaultMutableTreeNode parent, Vector<Object> tree, String path) {
		Vector<Object> children = new Vector<Object>();
		for (int i = 1; i < tree.size(); i++) {
			children.add(tree.get(i));
		}
		Collections.sort(children, PackageDialog::sortItems);
		String          name      = (String) tree.get(0);
		String          childPath = path.equals("") ? name : path + "::" + name;
		PackageTreeNode child     = new PackageTreeNode(childPath);
		parent.add(child);
		for (int i = 0; i < children.size(); i++) {
			Vector<Object> c = (Vector<Object>) children.get(i);
			populateTree(child, c, childPath);
		}
	}

	public static void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
		ArrayList<TreeNode> list = Collections.list(node.children());
		for (TreeNode treeNode : list) {
			setNodeExpandedState(tree, (DefaultMutableTreeNode) treeNode, expanded);
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

	public String getPath() {
		return path;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			selectNode();
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
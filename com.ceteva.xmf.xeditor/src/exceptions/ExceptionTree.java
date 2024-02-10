package exceptions;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class ExceptionTree extends JTree implements MouseListener {

	public ExceptionTree() {
		super();
		setShowsRootHandles(true);
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		addMouseListener(this);
	}

	public void add(DefaultMutableTreeNode child, DefaultMutableTreeNode parent) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.insertNodeInto(child, parent, parent.getChildCount());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			doubleClick(e.getX(),e.getY());
		}
	}

	private void doubleClick(int x, int y) {
		TreePath path = getSelectionPath();
		Object object = path.getLastPathComponent();
		if (object instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			object = node.getUserObject();
			if (object instanceof ExceptionNode) {
				ExceptionNode e = (ExceptionNode) object;
				e.doubleClick(x,y);
			}
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

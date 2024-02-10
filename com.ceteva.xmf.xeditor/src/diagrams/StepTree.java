package diagrams;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import console.Console;
import diagrams.filmstrips.FilmstripNode;
import diagrams.filmstrips.Step;
import diagrams.filmstrips.StepNode;

public class StepTree extends JTree implements MouseListener {

	private DiagramFrame frame;
	private Console      console;

	public StepTree(Console console, DefaultTreeModel treeModel, DiagramFrame frame) {
		super(treeModel);
		this.frame   = frame;
		this.console = console;
		ToolTipManager.sharedInstance().registerComponent(this);
		addMouseListener(this);
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getSelectionPath() != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
			if (node instanceof StepNode) {
				StepNode stepNode = (StepNode) node;
				Step step = stepNode.getStep();
				if (step.getDoc() != null && !step.getDoc().equals("")) {
					String doc = step.getDoc();
					doc = doc.trim();
					doc = doc.replaceAll("\n", "<br>");
					return "<html>" + doc + "</html>";
				}
			}
		}
		return super.getToolTipText(event);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
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

	public void expandAllNodes() {
		int j = getRowCount();
		int i = 0;
		while (i < j) {
			expandRow(i);
			i += 1;
			j  = getRowCount();
		}
	}

}

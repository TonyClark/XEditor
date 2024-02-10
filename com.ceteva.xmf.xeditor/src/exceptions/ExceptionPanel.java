package exceptions;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

public class ExceptionPanel extends JPanel {

	private ExceptionTree tree = new ExceptionTree();
	private ExceptionNode root = null;

	public ExceptionPanel() {
		setLayout(new BorderLayout());
		add(new JScrollPane(tree));
    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.setCellRenderer(new ExceptionTreeRenderer());
	}
	
	public void setRoot(ExceptionNode root) {
		if(this.root != null) {
			root.dispose();
		}
		this.root = root;
		root.createTree(tree);
	}

}

package files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

class FileSystemPopup extends JPopupMenu {

	private FileSystemNode node;

	public FileSystemPopup(FileSystemNode node) {
		this.node = node;
		if (node instanceof Renameable) {
			Renameable renameable = (Renameable) node;
			JMenuItem  rename     = new JMenuItem("Rename");
			rename.addActionListener((e) -> renameable.rename());
			add(rename);
		}
		if (node instanceof Deletable) {
			Deletable deletable = (Deletable) node;
			JMenuItem delete    = new JMenuItem("Delete");
			delete.addActionListener((e) -> deletable.delete());
			add(delete);
		}
	}

	public FileSystemNode getNode() {
		return node;
	}
}
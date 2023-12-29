package files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import qwerky.tools.filesearch.*;

public class DirectoryPopup extends FileSystemPopup {

	public DirectoryPopup(DirectoryNode node) {
		super(node);
		JMenu     add         = new JMenu("Add");
		JMenuItem addFile     = new JMenuItem("File");
		JMenuItem addDir      = new JMenuItem("Directory");
		JMenuItem addManifest = new JMenuItem("Manifest");
		JMenuItem addPackage  = new JMenuItem("Package");
		JMenuItem addClass    = new JMenuItem("Class");
		JMenuItem search      = new JMenuItem("Search");
		addFile.addActionListener((e) -> node.addFile());
		addDir.addActionListener((e) -> node.addDir());
		addManifest.addActionListener((e) -> node.addManifest());
		addPackage.addActionListener((e) -> node.addPackage());
		addClass.addActionListener((e) -> node.addClass());
		search.addActionListener((e) -> search());
		add.add(addFile);
		add.add(addDir);
		add.add(addManifest);
		add.add(addPackage);
		add.add(addClass);
		add(search);
		add(new JSeparator());
		add(add);
	}

	private void search() {
		DirectoryNode node   = (DirectoryNode) getNode();
		FileSearchGUI search = node.getConsole().getFileSearch();
		search.setSearchDir(node.getDir());
		search.show();
	}

}

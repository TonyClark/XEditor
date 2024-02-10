package files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class FilePopup extends FileSystemPopup {

	public FilePopup(FileNode node) {
		super(node);
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				node.load();
			}
		});
		add(new JSeparator());
		add(load);
		JMenuItem rename = new JMenuItem("Rename");
		rename.addActionListener((e) -> node.rename());
		add(new JSeparator());
		add(rename);
	}

}

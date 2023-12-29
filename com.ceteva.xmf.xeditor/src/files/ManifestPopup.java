package files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class ManifestPopup extends FilePopup {

	public ManifestPopup(ManifestNode node) {
		super(node);
		JMenu     touch       = new JMenu("Touch");
		JMenuItem touchSource = new JMenuItem("Source");
		touchSource.addActionListener((e) -> node.touchSource());
		touch.add(touchSource);
		JMenuItem touchBinaries = new JMenuItem("Binaries");
		touchBinaries.addActionListener((e) -> node.touchBinaries());
		touch.add(touchBinaries);
		add(touch);
	}

}

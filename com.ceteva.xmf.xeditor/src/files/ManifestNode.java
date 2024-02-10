package files;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.io.FileUtils;

import console.Console;

public class ManifestNode extends FileNode implements PopupHandler, Deletable {

	public ManifestNode(Console console, JTree tree, File file) {
		super(file, console, tree, "icons/manifest.png");
	}

	public void load() {
		getConsole().compileAndLoadManifest(getFile());
	}

	public void rename() {
		JOptionPane.showMessageDialog(getConsole(), "cannot rename a manifest");
	}

	public void touchBinaries() {
		SwingUtilities.invokeLater(() -> {
			Console.send("touchXMF", new Object[] { getFile().getParent(), false });
		});
	}

	public void touchSource() {
		SwingUtilities.invokeLater(() -> {
			Console.send("touchXMF", new Object[] { getFile().getParent(), true });
		});
	}

	@Override
	public void popup(MouseEvent e) {
		new ManifestPopup(this).show(e.getComponent(), e.getX(), e.getY());
	}
}

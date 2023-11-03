package console.browser;

import java.io.File;
import java.util.Vector;

import javax.swing.JSplitPane;

import console.Console;
import files.FileSystemWidget;

public class Browser extends JSplitPane {

	private static FileSystemWidget     fileSystem;
	private static ElementBrowserWidget elementBrowser;

	public Browser(Console console, String roots) {
		super(JSplitPane.VERTICAL_SPLIT, fileSystem = new FileSystemWidget(console, roots), elementBrowser = new ElementBrowserWidget(console));
		setDividerLocation(500);
	}

	public static FileSystemWidget getFileSystem() {
		return fileSystem;
	}

	public static ElementBrowser getElementBrowser() {
		return elementBrowser.getElementBrowser();
	}

	public void addChild(int parent, int child, String label) {
		elementBrowser.getElementBrowser().addChild(parent, child, label);
	}

	public void setRoot(Vector<Object> tree) {
		elementBrowser.setRoot(tree);
	}

	public void setElementImage(int id, String imageFile) {
		elementBrowser.setElementImage(id, imageFile);
	}

	public void refreshNode(int id) {
		elementBrowser.refresh(id);
	}

	public void setHoverText(int id, String text) {
		elementBrowser.setHoverText(id,text);
	}

	public void browseDir(File dir) {
		fileSystem.browseDir(dir);
	}

}

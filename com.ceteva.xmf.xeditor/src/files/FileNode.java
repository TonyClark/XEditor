package files;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.io.FileUtils;

import console.Console;

public class FileNode extends FileSystemNode implements PopupHandler, Deletable, Renameable {

	private File file;
	private int  caretPosition = 0;

	public FileNode(Console console, JTree tree, File file) {
		this(file, console, tree, "icons/editor.gif");
	}

	public FileNode(File file, Console console, JTree tree, String icon) {
		super(file, console, tree, icon);
		this.file = file;
	}

	public FileNode clone() {
		return new FileNode(file, getConsole(), getTree(), ""+getImage());
	}

	@Override
	public String toString() {
		return file.getName();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void popup(MouseEvent e) {
		new FilePopup(this).show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void delete() {
		int input = JOptionPane.showConfirmDialog(null, "Really delete " + file + "?");
		if (input == 0) {
			DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
			model.removeNodeFromParent(this);
			file.delete();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileNode) {
			FileNode d = (FileNode) obj;
			return file.equals(d.getFile());
		} else
			return super.equals(obj);
	}

	public void load() {
		getConsole().compileAndLoad(file);
	}

	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

	public int getCaretPosition() {
		return caretPosition;
	}

	public void rename() {
		String oldName = getFile().getName();
		String newName = JOptionPane.showInputDialog("Enter file name", oldName);
		if (newName != null) {
			File newFile = new File(getFile().getAbsolutePath().replace(oldName, newName));
			if (!newFile.exists()) {
				try {
					FileUtils.moveFile(getFile(), newFile);
					setFile(newFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				JOptionPane.showMessageDialog(getConsole(), newFile + " exists");
		}

	}

}

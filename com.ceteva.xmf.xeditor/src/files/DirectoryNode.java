package files;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import console.Console;

public class DirectoryNode extends FileSystemNode implements PopupHandler, Deletable, Renameable {

	private File dir;

	public DirectoryNode(Console console, JTree tree, File dir) {
		super(dir, console, tree, null);
		this.dir = dir;
	}
	
	public DirectoryNode clone() {
		return new DirectoryNode(getConsole(),getTree(),dir);
	}

	@Override
	public String toString() {
		return dir.getName();
	}

	@Override
	public void popup(MouseEvent e) {
		new DirectoryPopup(this).show(e.getComponent(), e.getX(), e.getY());
	}

	public void addFile() {
		String name = JOptionPane.showInputDialog("Enter file name", "");
		if (name != null) {
			if (!name.endsWith(".xmf"))
				name = name + ".xmf";
			File file = new File(dir.getAbsolutePath() + "/" + name);
			if (!file.exists()) {
				try {
					file.createNewFile();
					FileNode         node  = new FileNode(getConsole(), getTree(), file);
					DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
					model.insertNodeInto(node, this, this.getChildCount());
					model.reload(node);
					TreeNode[] nodes = model.getPathToRoot(node);
					TreePath   path  = new TreePath(nodes);
					getTree().scrollPathToVisible(path);
					getTree().setSelectionPath(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DirectoryNode) {
			DirectoryNode d = (DirectoryNode) obj;
			return dir.equals(d.getDir());
		} else
			return super.equals(obj);
	}

	public File getDir() {
		return dir;
	}

	public void addDir() {
		String name = JOptionPane.showInputDialog("Enter dir name", "");
		if (name != null) {
			File file = new File(dir.getAbsolutePath() + "/" + name);
			if (!file.exists()) {
				file.mkdirs();
				DirectoryNode    node  = new DirectoryNode(getConsole(), getTree(), file);
				DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
				model.insertNodeInto(node, this, this.getChildCount());
				model.reload(node);
				TreeNode[] nodes = model.getPathToRoot(node);
				TreePath   path  = new TreePath(nodes);
				getTree().scrollPathToVisible(path);
				getTree().setSelectionPath(path);
			}
		}
	}

	@Override
	public void delete() {
		int input = JOptionPane.showConfirmDialog(null, "Really delete " + dir + "?");
		if (input == 0) {
			DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
			model.removeNodeFromParent(this);
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addManifest() {
		int    input = JOptionPane.showConfirmDialog(null, "Really add a manifest ?");
		String name  = "Manifest.xmf";
		File   file  = new File(dir.getAbsolutePath() + "/" + name);
		if (!file.exists()) {
			try {
				file.createNewFile();
				ManifestNode     node  = new ManifestNode(getConsole(), getTree(), file);
				DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
				model.insertNodeInto(node, this, this.getChildCount());
				model.reload(node);
				TreeNode[] nodes = model.getPathToRoot(node);
				TreePath   path  = new TreePath(nodes);
				getTree().scrollPathToVisible(path);
				getTree().setSelectionPath(path);
				String template   = "parserImport Manifests;\n\n\n@Manifest " + dir.getName() + "\n\nend;\n";
				byte[] strToBytes = template.getBytes();
				Files.write(Paths.get(file.getAbsolutePath()), strToBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPackage() {
		String name = JOptionPane.showInputDialog("Enter package name", "");
		if (name != null) {
			if (!name.endsWith(".xmf"))
				name = name + ".xmf";
			File file = new File(dir.getAbsolutePath() + "/" + name);
			if (!file.exists()) {
				try {
					file.createNewFile();
					FileNode         node  = new FileNode(getConsole(), getTree(), file);
					DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
					model.insertNodeInto(node, this, this.getChildCount());
					model.reload(node);
					TreeNode[] nodes = model.getPathToRoot(node);
					TreePath   path  = new TreePath(nodes);
					getTree().scrollPathToVisible(path);
					getTree().setSelectionPath(path);
					String template   = "parserImport XOCL;\n\n\ncontext Root\n  @Package " + dir.getName() + "\n\n  end\n";
					byte[] strToBytes = template.getBytes();
					Files.write(Paths.get(file.getAbsolutePath()), strToBytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addClass() {
		String name = JOptionPane.showInputDialog("Enter class name", "");
		if (name != null) {
			if (!name.endsWith(".xmf"))
				name = name + ".xmf";
			File file = new File(dir.getAbsolutePath() + "/" + name);
			String className = name.replace(".xmf","");
			if (!file.exists()) {
				try {
					file.createNewFile();
					FileNode         node  = new FileNode(getConsole(), getTree(), file);
					DefaultTreeModel model = (DefaultTreeModel) getTree().getModel();
					model.insertNodeInto(node, this, this.getChildCount());
					model.reload(node);
					TreeNode[] nodes = model.getPathToRoot(node);
					TreePath   path  = new TreePath(nodes);
					getTree().scrollPathToVisible(path);
					getTree().setSelectionPath(path);
					String template   = "parserImport XOCL;\n\n\ncontext Root\n  @Class " + className + "\n\n  end\n";
					byte[] strToBytes = template.getBytes();
					Files.write(Paths.get(file.getAbsolutePath()), strToBytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void rename() {
		String oldName = dir.getName();
		String newName = JOptionPane.showInputDialog("Enter directory name", oldName);
		if (newName != null) {
			File newFile = new File(dir.getAbsolutePath().replace(oldName, newName));
			if (!newFile.exists()) {
				dir.renameTo(newFile);
				dir = newFile;
			} else
				JOptionPane.showMessageDialog(getConsole(), newFile + " exists");
		}

	}

}

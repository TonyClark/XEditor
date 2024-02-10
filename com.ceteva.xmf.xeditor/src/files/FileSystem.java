package files;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import console.Console;

public class FileSystem extends JTree implements MouseListener, TreeCellRenderer {

	public static int compareFiles(File f1, File f2) {
		if (f1.isDirectory() && !f2.isDirectory())
			return -1;
		else if (!f1.isDirectory() && f2.isDirectory())
			return 1;
		else
			return f1.getName().compareTo(f2.getName());
	}

	public static void removeAllChildren(final DefaultTreeModel model, final DefaultMutableTreeNode node) {
		if (model == null) {
			throw new NullPointerException("model == null");
		}
		if (node == null) {
			throw new NullPointerException("node == null");
		}
		synchronized (model) {
			int count = node.getChildCount();
			List<TreeNode> children = new ArrayList<>(count);
			int[] indices = new int[count];
			for (int i = 0; i < count; i++) {
				children.add(node.getChildAt(i));
				indices[i] = i;
			}
			node.removeAllChildren();
			model.nodesWereRemoved(node, indices, children.toArray());
		}
	}

	private Console  console;
	private FileNode selectedFile;

	public FileSystem(Console console, String roots) {
		this.console = console;
		setModel(new DefaultTreeModel(createTree(roots)));
		addMouseListener(this);
		setRootVisible(false);
		setCellRenderer(this);
		setExpandsSelectedPaths(true);
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		setTransferHandler(new TreeTransferHandler());
		setShowsRootHandles(true);
	}

	public void add(DefaultMutableTreeNode child, DefaultMutableTreeNode parent) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.insertNodeInto(child, parent, parent.getChildCount());
	}

	private void add(DefaultMutableTreeNode child, DefaultMutableTreeNode parent, int index) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.insertNodeInto(child, parent, index);
	}

	public void addDefiniens(DefiniensNode node) {
		File file = new File(node.getDefiniens().getFile());
		FileNode fileNode = getFileNode(file, (DefaultMutableTreeNode) getModel().getRoot());
		if (fileNode != null)
			add(node, fileNode);
	}

	public void addDefiniens(DefiniensNode node, DefiniensNode parent) {
		int index = 0;
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(index);
			if (child instanceof DefiniensNode) {
				DefiniensNode childDef = (DefiniensNode) child;
				if (childDef.getDefiniens().getName().compareTo(node.getDefiniens().getName()) < 0) {
					index++;
				} else
					break;
			}
		}
		add(node, parent, index);
	}

	public void clear(File file) {
		FileNode fileNode = getFileNode(file, (DefaultMutableTreeNode) getModel().getRoot());
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		if (fileNode != null)
			removeAllChildren(model, fileNode);
	}

	private DefaultMutableTreeNode createTree(File root) {
		if (root.exists() && root.isDirectory()) {
			DirectoryNode dir = new DirectoryNode(console, this, root);
			File[] files = root.listFiles();
			java.util.Arrays.sort(files, FileSystem::compareFiles);
			for (File file : files) {
				if (file.isDirectory() || file.getName().endsWith("xmf"))
					dir.add(createTree(file));
			}
			return dir;
		} else if (root.exists()) {
			if (root.getName().startsWith("Manifest"))
				return new ManifestNode(console, this, root);
			else
				return new FileNode(console, this, root);
		} else
			throw new Error("cannot find: " + root);
	}

	private DefaultMutableTreeNode createTree(String roots) {
		FileSystemNode fileSystem = new FileSystemNode(this, console, this, null);
		for (String folder : roots.split(":")) {
			fileSystem.add(createTree(new File(folder)));
		}
		return fileSystem;
	}

	private void doubleClick() {
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		if (n instanceof FileNode) {
			if (selectedFile != null) {
				console.saveCaretPosition(selectedFile);
			}
			FileNode fileNode = (FileNode) n;
			selectedFile = fileNode;
			console.load(fileNode.getFile());
			console.setCaretFileNodePosition(selectedFile);
		}
	}

	private FileNode getFileNode(File file, DefaultMutableTreeNode node) {
		if (node instanceof FileNode) {
			FileNode fileNode = (FileNode) node;
			try {
				if (fileNode.getFile().getCanonicalPath().equals(file.getCanonicalPath()))
					return fileNode;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			FileNode fileNode = getFileNode(file, (DefaultMutableTreeNode) node.getChildAt(i));
			if (fileNode != null)
				return fileNode;
		}
		return null;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof FileSystemNode) {
			FileSystemNode fsNode = (FileSystemNode) value;
			if (fsNode.getImage() != null) {
				JLabel label = new JLabel(value.toString());
				if (hasFocus) {
					label.setOpaque(true);
					label.setBackground(new Color(0, 153, 0));
					label.setForeground(Color.white);
				}
				label.setIcon(fsNode.getImage());
				return label;
			}
		}
		if (value instanceof FileNode) {
			ImageIcon icon = new ImageIcon("icons/editor.gif");
			JLabel label = new JLabel(value.toString());
			label.setIcon(icon);
			if (hasFocus) {
				label.setOpaque(true);
				label.setBackground(new Color(0, 153, 0));
				label.setForeground(Color.white);
			}
			return label;
		} else if (value instanceof DefiniensNode) {
			ImageIcon icon = new ImageIcon("icons/def.gif");
			JLabel label = new JLabel(value.toString());
			label.setIcon(icon);
			if (hasFocus) {
				label.setOpaque(true);
				label.setBackground(new Color(0, 153, 0));
				label.setForeground(Color.white);
			}
			return label;
		} else
			return new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.isPopupTrigger()) {
				popup(e);
			} else {
				if (e.getClickCount() >= 2)
					doubleClick();
				if (e.getClickCount() == 1)
					singleClick();
			}
		} catch (Exception x) {
			System.err.println(x);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup(e);
		} else {
			int row = getRowForLocation(e.getX(), e.getY());
			if (row == -1) // When user clicks on the "empty surface"
				clearSelection();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void popup(MouseEvent e) {
		TreePath path = getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (node instanceof PopupHandler) {
				PopupHandler handler = (PopupHandler) node;
				handler.popup(e);
			}
		} else {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem browse = new JMenuItem("Browse");
			popup.add(browse);
			browse.addActionListener((x) -> console.browse());
			popup.show(this, e.getX(), e.getY());
		}
	}

	private void singleClick() {
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		if (n instanceof DefiniensNode) {
			DefiniensNode node = (DefiniensNode) n;
			File file = new File(node.getDefiniens().getFile());
			console.setCaretPosition(node.getDefiniens().getLineStart());
		}
	}

	public void select(File file) {
		FileNode node = findNode(file, (DefaultMutableTreeNode) getModel().getRoot());
		if (node != null) {
			selectedFile = node;
			SwingUtilities.invokeLater(() -> {
				TreePath path = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot(node));
				setSelectionPath(path);
				scrollPathToVisible(path);
			});
		}
	}

	public final FileNode findNode(File file, DefaultMutableTreeNode node) {
		if (node instanceof FileNode) {
			FileNode fileNode = (FileNode) node;
			if (file.equals(fileNode.getFile())) {
				return fileNode;
			}
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			FileNode found = findNode(file, (DefaultMutableTreeNode) node.getChildAt(i));
			if (found != null)
				return found;
		}
		return null;
	}

	public void browseDir(File dir) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		add(createTree(dir), root);
	}

	public static String relativize(String path) {
		// Paths should be relative to the root directory if they are part of the system.
		java.nio.file.FileSystem system = FileSystems.getDefault();
		Path here = system.getPath(".").toAbsolutePath().normalize();
		Path root = system.getPath("..").toAbsolutePath().normalize();
		Path target = system.getPath(path).toAbsolutePath().normalize();
		if(target.startsWith(root)) {
			return here.relativize(target).toString();
		}
		else return target.toString();
	}

}

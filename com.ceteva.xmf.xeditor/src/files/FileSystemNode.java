package files;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;

import console.Console;
import editor.EditorTextArea;

public class FileSystemNode extends DefaultMutableTreeNode {

	private static Hashtable<String, ImageIcon> images = new Hashtable<String, ImageIcon>();

	private JTree     tree;
	private Console   console;
	private ImageIcon image = null;

	public FileSystemNode(Object data, Console console, JTree tree, String image) {
		super(data);
		this.tree    = tree;
		this.console = console;
		this.image   = createImage(image);
	}

	public DefaultMutableTreeNode clone() {
		return new FileSystemNode(getUserObject(), console, tree, "" + image);
	}

	private ImageIcon createImage(String image) {
		if (image == null)
			return null;
		else {
			if (isCachedImage(image))
				return getImage(image);
			else {
				File file = new File(image);
				if (!file.exists()) {
					return null;
				} else {
					ImageIcon icon = new ImageIcon(file.getAbsolutePath());
					if (icon.getIconWidth() > 15 || icon.getIconHeight() > 15)
						icon = new ImageIcon(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
					images.put(image, icon);
					return this.image;
				}
			}
		}
	}

	public JTree getTree() {
		return tree;
	}

	public Console getConsole() {
		return console;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public void cacheImage(String key, ImageIcon image) {
		images.put(key, image);
	}

	public boolean isCachedImage(String key) {
		return images.containsKey(key);
	}

	public ImageIcon getImage(String key) {
		return images.get(key);
	}

	public void browse() {
		console.browse();
	}

	public FileSystemNode copyInto(DirectoryNode dirNode, boolean remove) {
		return this;
	}

}

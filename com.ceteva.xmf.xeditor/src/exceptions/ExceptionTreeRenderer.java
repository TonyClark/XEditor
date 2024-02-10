package exceptions;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import filtertree.ImageProvider;

public class ExceptionTreeRenderer extends DefaultTreeCellRenderer {

	private Hashtable<String, ImageIcon> images = new Hashtable<String, ImageIcon>();

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
					return icon;
				}
			}
		}
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

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		value = node.getUserObject();

		if (value instanceof Tooltipable) {
			setToolTipText(((Tooltipable) value).getToolTip());
		}

		Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		Icon icon = getIcon();
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		if (value instanceof ImageProvider) {
			ImageProvider i = (ImageProvider) value;
			String imageFile = i.getImageFile();
			setIcon(createImage(imageFile));
		}

		return component;
	}
}

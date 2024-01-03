package filtertree;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Hashtable;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TradingProjectTreeRenderer extends DefaultTreeCellRenderer {

	private Hashtable<String, ImageIcon> imageCache   = new Hashtable<String, ImageIcon>();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		Icon icon = getIcon();
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		if (value instanceof ImageProvider) {
			ImageProvider i         = (ImageProvider) value;
			String        imageFile = i.getImageFile();
			if (imageFile != null) {
				if (!imageCache.containsKey(imageFile)) {
					imageCache.put(imageFile, new ImageIcon(new ImageIcon(imageFile).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
				}
				setIcon(imageCache.get(imageFile));
			}
		}
		return this;
	}
}
package diagrams.model;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.DefaultCheckboxTreeCellRenderer;

public class VisibilityTreeCellRenderer extends DefaultCheckboxTreeCellRenderer {

	private ImageIcon p;
	private ImageIcon c;
	private ImageIcon a;

	public VisibilityTreeCellRenderer(int size,Font font) {
		this.p = new ImageIcon(new ImageIcon("icons/package.png").getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
		this.c = new ImageIcon(new ImageIcon("icons/class.png").getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
		this.a = new ImageIcon(new ImageIcon("icons/attribute.png").getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
		setFont(font);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object object = node.getUserObject();
			if (object instanceof Package) {
				label.setIcon(p);
			}
			if (object instanceof Class) {
				label.setIcon(c);
			}
			if (object instanceof Field) {
				label.setIcon(a);
			}
		}
		label.setFont(getFont());
		return component;
	}

}

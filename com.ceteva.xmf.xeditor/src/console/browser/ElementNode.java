package console.browser;

import javax.swing.tree.DefaultMutableTreeNode;

import filtertree.HoverProvider;
import filtertree.ImageProvider;

public class ElementNode extends DefaultMutableTreeNode implements ImageProvider, HoverProvider {

	private int    elementId;
	private String label;
	private String imageFile = null;
	private String hover     = null;

	public ElementNode(int elementId, String label) {
		super(label);
		this.elementId = elementId;
		this.label = label;
	}

	public DefaultMutableTreeNode clone() {
		return new ElementNode(elementId, label);
	}

	public int getElementId() {
		return elementId;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String getImageFile() {
		return imageFile;
	}

	@Override
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	@Override
	public String getText() {
		return hover;
	}

	@Override
	public void setText(String text) {
		hover = text;
	}

}

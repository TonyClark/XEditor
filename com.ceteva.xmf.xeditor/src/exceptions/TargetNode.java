package exceptions;

import java.util.Vector;

import filtertree.ImageProvider;

public class TargetNode extends ExceptionNode implements Tooltipable, ImageProvider {

	private String type;

	public TargetNode(String label, String type, Vector<ExceptionNode> children) {
		super(label, children);
		this.type = type;
	}

	@Override
	public String getImageFile() {
		return "icons/target.png";
	}

	@Override
	public void setImageFile(String imageFile) {

	}

	@Override
	public String getToolTip() {
		return "target of type " + type;
	}

}

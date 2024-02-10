package exceptions;

import java.util.Vector;

import filtertree.ImageProvider;

public class CodeBoxNode extends ExceptionNode implements Tooltipable, ImageProvider{

	public CodeBoxNode(String label, Vector<ExceptionNode> children) {
		super(label, children);
	}

	@Override
	public String getImageFile() {
		return "icons/code_box.png";
	}

	@Override
	public void setImageFile(String imageFile) {

	}

	@Override
	public String getToolTip() {
		return "code box";
	}


}

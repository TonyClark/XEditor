package exceptions;

import java.util.Vector;
import filtertree.*;

public class ArgNode extends LocalNode implements Tooltipable, ImageProvider {

	private String type;

	public ArgNode(String name, String value, String type, int max, Vector<ExceptionNode> children) {
		super(name, value, max, children);
		this.type = type;
	}

	@Override
	public String getToolTip() {
		return "argument declared type " + type;
	}

	@Override
	public String getImageFile() {
		return "icons/argument.png";
	}

	@Override
	public void setImageFile(String imageFile) {
		
	}

}

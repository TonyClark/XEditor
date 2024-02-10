package exceptions;

import java.util.Vector;

import filtertree.ImageProvider;

public class LocalNode extends ExceptionNode implements Tooltipable, ImageProvider {

	private String name;
	private String value;

	public LocalNode(String name, String value, int max, Vector<ExceptionNode> children) {
		super(LPad(name, max, ' ') + " = " + value, children);
		this.name  = name;
		this.value = value;
	}

	public static String LPad(String str, Integer length, char car) {
	  return (str + String.format("%" + length + "s", "").replace(" ", String.valueOf(car))).substring(0, length);
	}

	@Override
	public String getToolTip() {
		return "local variable ";
	}

	@Override
	public String getImageFile() {
		return "icons/local.png";
	}

	@Override
	public void setImageFile(String imageFile) {
		
	}

}

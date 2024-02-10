package exceptions;

import java.util.Vector;

public class PropertyNode extends LocalNode {

	private String name;
	private String value;

	public PropertyNode(String name, String value, int max, Vector<ExceptionNode> children) {
		super(name,value,max,children);
		this.name  = name;
		this.value = value;
	}

	public static String LPad(String str, Integer length, char car) {
	  return (str + String.format("%" + length + "s", "").replace(" ", String.valueOf(car))).substring(0, length);
	}

	@Override
	public String getToolTip() {
		return "slot";
	}

	@Override
	public String getImageFile() {
		return "icons/property.png";
	}

	@Override
	public void setImageFile(String imageFile) {
		
	}

}

package exceptions;

import java.util.Vector;
import console.Console;
import filtertree.ImageProvider;

public class ResourceNode extends ExceptionNode implements Tooltipable, ImageProvider {

	private String file;
	private int    line;
	private int charPos;

	public ResourceNode(String file, int line, int charPosn) {
		super(file, new Vector<ExceptionNode>());
		this.file    = file;
		this.line    = line;
		this.charPos = charPos;
	}

	@Override
	public String getImageFile() {
		return "icons/file_search.png";
	}

	@Override
	public void setImageFile(String imageFile) {

	}

	@Override
	public String getToolTip() {
		return "double click to edit file";
	}

	public void doubleClick(int x, int y) {
		Console.CONSOLE.loadPath(file);
		Console.CONSOLE.setLinePosition(line);
	}

}
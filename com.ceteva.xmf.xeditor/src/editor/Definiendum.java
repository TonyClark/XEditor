package editor;

import java.awt.Point;

public class Definiendum implements Located {

	String name;
	int lineStart;
	int lineEnd;
	int defStart;
	int defEnd;

	public Definiendum(String name, int lineStart, int lineEnd, int defStart, int defEnd) {
		super();
		this.name = name;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		this.defStart = defStart;
		this.defEnd = defEnd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLineStart() {
		return lineStart;
	}

	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public int getDefStart() {
		return defStart;
	}

	public void setDefStart(int defStart) {
		this.defStart = defStart;
	}

	public int getDefEnd() {
		return defEnd;
	}

	public void setDefEnd(int defEnd) {
		this.defEnd = defEnd;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFile(String file) {
		// TODO Auto-generated method stub

	}

	public boolean hasDefiniens(Point d) {
		return d.getX() == lineStart && d.getY() == lineEnd;
	}

}

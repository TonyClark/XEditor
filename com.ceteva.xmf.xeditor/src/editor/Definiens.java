package editor;

import java.io.File;

public class Definiens implements Located {

	private String  name;
	private int     lineStart;
	private int     lineEnd;
	private File    file;
	private boolean isRef;

	public Definiens(String name, int lineStart, int lineEnd, File file, boolean isRef) {
		super();
		this.name = name;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		this.file = file;
		this.isRef = isRef;
	}

	@Override
	public String getFile() {
		return file.getAbsolutePath();
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public int getLineStart() {
		return lineStart;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRef() {
		return isRef;
	}

	@Override
	public void setFile(String file) {
		this.file = new File(file);
	}

	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}

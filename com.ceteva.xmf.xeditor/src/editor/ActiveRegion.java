package editor;

import java.awt.Graphics;

import javax.swing.JMenuItem;

public abstract class ActiveRegion {

	private int       start, end;
	private JMenuItem item;

	public ActiveRegion(int start, int end, JMenuItem item) {
		super();
		this.start = start;
		this.end = end;
		this.item = item;
	}

	public JMenuItem getItem() {
		return item;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public abstract void highlight(Graphics g, EditorTextArea editorTextArea);

	public boolean contains(int index) {
		return start <= index && index <= end;
	}

}

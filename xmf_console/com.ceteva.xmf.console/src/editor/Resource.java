package editor;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;

public class Resource extends ActiveRegion {

	public Resource(int start, int end, JMenuItem item) {
		super(start, end, item);
	}

	@Override
	public void highlight(Graphics g, EditorTextArea editor) {
		try {
			Rectangle2D rStart = editor.modelToView2D(getStart());
			Rectangle2D rEnd   = editor.modelToView2D(getEnd());
			int         width  = (int) (rEnd.getX() - rStart.getX());
			int         height = (int) (rEnd.getY() - rStart.getY());
			g.drawRect((int) rStart.getX(), (int) rStart.getY(), width,height);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package tabs;

import java.awt.Graphics;

import javax.swing.JLabel;

public abstract class TabAction {

	private int			width;
	private boolean	isOver	= false;
	private String	toolTip	= "";

	public TabAction(String toolTip, int width) {
		super();
		this.width = width;
		this.toolTip = toolTip;
	}

	public String getToolTip() {
		return toolTip;
	}

	public int getWidth() {
		return width;
	}

	public boolean isOver() {
		return isOver;
	}

	public abstract void perform(JLabel label);

	public void setIsOver(boolean isOver) {
		this.isOver = isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

}

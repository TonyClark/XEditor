package inspect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RubberBand extends Rectangle {

	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
		this.width = 0;
		this.height = 0;
	}

	public void update(int x, int y) {
		width = x - this.x;
		height = y - this.y;
	}

	public void paint(Graphics2D g2) {
		if (height > 0 && width > 0) {
			g2.setColor(Color.black);
			g2.drawRect(x, y, width, height);
		}
	}

	public boolean isActive() {
		return height > 0 && width > 0;
	}

	@Override
	public String toString() {
		return "RubberBand [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}

}

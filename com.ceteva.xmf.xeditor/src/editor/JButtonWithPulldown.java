package editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class JButtonWithPulldown extends JButton implements MouseListener, MouseMotionListener {

	public static final int				PAD			= 5;

	public static final int				WIDTH		= 6;
	public static final int				HEIGHT	= 6;

	private int										mouseX;
	private int										mouseY;
	private int										staticWidth;
	private Consumer<JComponent>	pulldown;

	public JButtonWithPulldown(ImageIcon icon, int staticWidth, Consumer<JComponent> pulldown) {
		super(icon);
		this.staticWidth = staticWidth;
		this.pulldown = pulldown;
		addMouseListener(this);
		addMouseMotionListener(this);
		Border emptyBorder = BorderFactory.createEmptyBorder();
		setBorder(emptyBorder);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseX = 0;
		mouseY = 0;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public boolean isOverPulldown() {
		return mouseX > staticWidth && mouseX < getWidth() && mouseY > 0 && mouseY < getHeight();
	}

}

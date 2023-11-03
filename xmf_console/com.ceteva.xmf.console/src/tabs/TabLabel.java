package tabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class TabLabel extends JLabel implements MouseListener, MouseMotionListener {

	public interface LabelAction {
		void perform();
	}

	private static int SIZE = 15;

	private static final Icon	close							= new ImageIcon(new ImageIcon("icons/close.png").getImage().getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH));
	private Icon							icon;
	private boolean						hasErrors					= false;
	private String						backgroundToolTip	= null;
	private LabelAction				closeAction;
	private LabelAction				selectAction;
	private TabAction[]				tabActions;

	public TabLabel(String label, String path, LabelAction closeAction,LabelAction selectAction, TabAction... tabActions) {
		super(label);
		this.tabActions = tabActions;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH));
		ImageIcon i = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH));
		setIcon(icon);
		setIconTextGap(5);
		addMouseListener(this);
		addMouseMotionListener(this);
		calculateSize();
		this.closeAction = closeAction;
		this.selectAction = selectAction;
	}

	private void calculateSize() {
		int width = getPreferredSize().width;
		for (TabAction a : tabActions) {
			width += a.getWidth();
		}
		setPreferredSize(new Dimension(width, getHeight()));
	}

	private TabAction getTabAction(int x, int y) {
		int width = getPreferredSize().width;
		for (int i = tabActions.length - 1; i >= 0; i--) {
			if (x < width && x >= width - tabActions[i].getWidth())
				return tabActions[i];
			else
				width -= tabActions[i].getWidth();
		}
		return null;
	}

	private boolean isOverCloseIcon(int x, int y) {
		return x < 10;
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (isOverCloseIcon(x, y)) {
			try {
				closeAction.perform();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			TabAction action = getTabAction(x, y);
			if (action != null) {
				action.perform(this);
			} else selectAction.perform();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		setIcon(close);
	}

	public void mouseExited(MouseEvent e) {
		setIcon(icon);
		resetActions();
	}

	public void mouseMoved(MouseEvent e) {
		TabAction action = getTabAction(e.getX(), e.getY());
		if (action != null) {
			action.setIsOver(true);
			setToolTipText(action.getToolTip());
		} else {
			resetActions();
			setToolTipText(backgroundToolTip);
		}
		repaint();
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private void resetActions() {
		for (TabAction a : tabActions) {
			a.setIsOver(false);
		}
	}

	public void setBackgroundToolTipText(String toolTip) {
		backgroundToolTip = toolTip;
		setToolTipText(toolTip);
	}

	public void setErrors(boolean hasErrors) {
		setForeground(hasErrors ? Color.RED : Color.BLACK);
	}

}

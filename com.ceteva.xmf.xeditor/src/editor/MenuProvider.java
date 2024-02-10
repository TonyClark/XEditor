package editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public interface MenuProvider {

	int	WIDTH		= 20;
	int	HEIGHT	= 20;

	public static interface ButtonAction {
		public void doIt();
	}

	public JPopupMenu getMenu(JPopupMenu menu);

	public static void doAction(ButtonAction action) {
		SwingUtilities.invokeLater(() -> {
			try {
				action.doIt();
			} catch (Throwable t) {
				System.err.println(t);
			}
		});
	}

	public static JButton getButton(String name, String tooltip, KeyStroke keyStroke, ButtonAction doIt) {
		Action action = new AbstractAction(name) {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAction(doIt);
			}
		};
		action.putValue(Action.ACTION_COMMAND_KEY, keyStroke);
		JButton button = new JButton(name);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.getActionMap().put(name, action);
		button.addActionListener(action);
		button.setToolTipText(tooltip + ((keyStroke != null) ? "(" + keyStroke + ")" : ""));
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACTION_COMMAND_KEY), name);
		return button;
	}

	public static JButton getImageButton(String path, String tooltip, ButtonAction doIt) {
		return getImageButton(path, tooltip, doIt, null);
	}

	public static JButton getImageButton(String path, String tooltip, ButtonAction doIt, Consumer<JComponent> pulldown) {
		return getImageButton(path, tooltip, null, doIt, pulldown);
	}

	public static ImageIcon getIcon(String path) {
		ImageIcon icon = new ImageIcon(path);
		return new ImageIcon(icon.getImage().getScaledInstance(WIDTH, HEIGHT, java.awt.Image.SCALE_SMOOTH));
	}

	public static ImageIcon drawTriangle(ImageIcon icon) {
		int pad = 2;
		int arrowWidth = 6;
		int arrowHeight = 6;
		Image image1 = icon.getImage();
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		int mid = (h/2);
		Image image = new BufferedImage(w + (2*pad) + arrowWidth, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ((BufferedImage) image).createGraphics();
		g2.drawImage(image1, 0, 0, null);
		int x1 = w + pad;
		int x2 = w + pad + arrowWidth;
		int x3 = w + pad + (arrowWidth/2);
		int y1 = mid - (arrowHeight/2);
		int y2 = mid - (arrowHeight/2);
		int y3 = mid + (arrowHeight/2);
		g2.setColor(Color.black);
		g2.fillPolygon(new int[] {x1,x2,x3},new int[] {y1,y2,y3},3);
		g2.dispose();
		return new ImageIcon(image);
	}

	public static ImageIcon getPullDownIcon(String path) {
		ImageIcon icon1 = new ImageIcon(path);
		icon1 = new ImageIcon(icon1.getImage().getScaledInstance(WIDTH, HEIGHT, java.awt.Image.SCALE_SMOOTH));
		return drawTriangle(icon1);
	}

	public static JButton getImageButton(String path, String tooltip, KeyStroke keyStroke, ButtonAction doIt, Consumer<JComponent> pulldown) {
		Action action = new AbstractAction(path) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (e.getSource() instanceof JButtonWithPulldown) {
							JButtonWithPulldown b = (JButtonWithPulldown) e.getSource();
							if (b.isOverPulldown()) pulldown.accept(b); else doIt.doIt();
						} else
							doIt.doIt();
					}
				});
			}
		};

		JButton button = pulldown == null ? new JButton(getIcon(path)) : new JButtonWithPulldown(getPullDownIcon(path), WIDTH, pulldown);
		button.getActionMap().put(path, action);
		button.addActionListener(action);
		button.setToolTipText(tooltip + ((keyStroke != null) ? " (" + keyStroke + ")" : ""));
		if (keyStroke != null) {
			action.putValue(Action.ACTION_COMMAND_KEY, keyStroke);
			button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACTION_COMMAND_KEY), path);
		}
		button.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		button.setContentAreaFilled(false);
		return button;
	}

}

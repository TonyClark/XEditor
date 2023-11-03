package editor;

import java.awt.Color;
import java.io.File;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

public class StatusBar extends JPanel implements MessageHandler {

	private interface Delayed {
		public void perform();
	}

	private static final int ACTIVITY_LENGTH = 50;

	private static int DELAY = 1000;

	private JLabel label;
	private JLabel dirty;
	private JLabel activity;
	private long startActivityTime;
	private Color background;
	private LinkedList<String> activityQueue = new LinkedList<String>();

	public StatusBar() {
		label = new JLabel("Ready");
		dirty = new JLabel("                   ");
		activity = new JLabel(activityString(" "));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(label);
		add(Box.createHorizontalGlue());
		add(dirty);
		add(Box.createHorizontalGlue());
		add(activity);
		background = getBackground();
	}

	private void displayActivity(String info) {
		info = activityString(info);
		synchronized (activityQueue) {
			if (activityQueue.isEmpty()) {
				activityQueue.addLast(info);
				activity.setText(info);
				activity.paintImmediately(activity.getVisibleRect());
				delay(() -> resetActivity());
			} else {
				activityQueue.addLast(info);
			}
		}
	}

	private String activityString(String s) {
		if (s.length() < ACTIVITY_LENGTH)
			return StringUtils.leftPad(s, ACTIVITY_LENGTH, ' ');
		else
			return StringUtils.truncate(s, ACTIVITY_LENGTH);
	}

	private void delay(Delayed d) {
		new Thread(() -> {
			synchronized (this) {
				try {
					wait(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			d.perform();
		}).start();
	}

	private synchronized void resetActivity() {
		synchronized (activityQueue) {
			activityQueue.removeFirst();
			activity.setText(activityString(" "));
			activity.paintImmediately(activity.getVisibleRect());
			if (!activityQueue.isEmpty()) {
				activity.setText(activityString(activityQueue.peekFirst()));
				activity.paintImmediately(activity.getVisibleRect());
				delay(() -> resetActivity());
			}
		}
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setDirty(boolean dirty) {
		this.dirty.setText(dirty ? "Modified" : "             ");
		if (dirty)
			setBackground(Color.PINK);
		else
			setBackground(background);
	}

	@Override
	public void handleMessage(String message) {
		label.setText(message);
	}

	public String getMessage() {
		return label.getText();
	}

	public void startActivity(String name, long time) {
		startActivityTime = time;
		displayActivity(name + "...");
	}

	public void endActivity(String name, long time) {
		displayActivity(name + " completed in " + (time - startActivityTime) + " ms");
	}

	public void displayError(String message) {
		displayActivity(message);
	}

	public void setFile(File file) {
		label.setText("Edit: " + file.getName());
	}

}
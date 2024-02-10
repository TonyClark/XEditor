package editor;

import java.awt.Color;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel implements MessageHandler {

	private static String messages = "";

	private JLabel  label;
	private Color   background;
	private Color   racingGreen  = new Color(0x01, 0x46, 0x00);
	private long    startTime;
	private JButton showMessages = new JButton("Messages");

	public StatusBar() {
		label = new JLabel("Ready");
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(label);
		add(Box.createHorizontalGlue());
		add(showMessages);
		showMessages.addActionListener((e) -> showMessages());
		background = getBackground();
	}

	private void showMessages() {
		setBackground(background);
		new MessageDialog(messages);
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	@Override
	public void handleMessage(String message) {
		label.setText(message);
	}

	public String getMessage() {
		return label.getText();
	}

	public void setFile(File file) {
		label.setText("Edit: " + file.getName());
	}

	public void displayError(String message) {
		messages += "ERROR " + message + "\n";
	}

	public void endActivity(String message, long endTime) {
		setBackground(background);
		messages += "END " + message + " after " + (endTime - startTime) + "ms\n";
	}

	public void startActivity(String message, long startTime) {
		setBackground(racingGreen);
		this.startTime  = startTime;
		messages       += "START " + message + "\n";
	}

}
package repl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class NameResolution extends JFrame implements MouseListener {

	private static Font FONT = new Font("Roman", Font.PLAIN, 8);
	private static Font BOLD = new Font("Roman", Font.BOLD, 8);
	private static int GAP = 20;
	private static int HEIGHT = 500;

	private Vector<String> names;
	private Vector<JLabel> labels = new Vector<JLabel>();
	private int nameCount = 0;
	private String lastName = "";
	private String prefix = "";
	private JPanel panel = new JPanel();
	private int x, y;
	private ConsoleTextArea editor;

	public NameResolution(int x, int y, Vector<String> names, ConsoleTextArea editor) {
		this.x = x;
		this.y = y;
		this.names = names;
		this.editor = editor;
		try {
			Collections.sort(names);
		} catch (Exception e) {
		}
		setLayout(new BorderLayout());
		setUndecorated(true);
		setOpacity(0.90f);
		add(new JScrollPane(panel), BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.removeAll();
		populatePanel();
		pack();
		int height = Math.min(HEIGHT, getHeight());
		setSize(getWidth(), height);
		setLocation(x, y - (height + GAP));
		setVisible(true);
		setAlwaysOnTop(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		editor.grabFocus();
	}

	private void populatePanel() {
		nameCount = 0;
		lastName = "";
		labels.clear();
		for (String name : names) {
			if (prefix.equals("") || name.startsWith(prefix)) {
				if (!(name.equals(lastName))) {
					lastName = name;
					nameCount++;
				}
				JLabel label = new JLabel(name);
				label.setFont(FONT);
				panel.add(label);
				label.addMouseListener(this);
				labels.add(label);
			}
		}
	}

	public void insertChar(char c) {
		prefix += c;
		panel.removeAll();
		populatePanel();
		pack();
		int height = Math.min(HEIGHT, getHeight());
		setSize(getWidth(), height);
		setLocation(x, y - (height + GAP));
		repaint();
	}

	public void deleteChar() {
		if (!(prefix.equals(""))) {
			prefix = prefix.substring(0, prefix.length() - 1);
			panel.removeAll();
			populatePanel();
			pack();
			int height = Math.min(HEIGHT, getHeight());
			setSize(getWidth(), height);
			setLocation(x, y - (height + GAP));
			repaint();
		} else {
			editor.resetNames();
		}
	}

	public boolean isSingleton() {
		return nameCount == 1;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Component component = e.getComponent();
		if (e.getClickCount() >= 2) {
			if (component instanceof JLabel) {
				JLabel label = (JLabel) component;
				editor.doubleClick(label.getText());
			}
		} else {
			if (component instanceof JLabel) {
				for (JLabel l : labels) {
					l.setFont(FONT);
				}
				JLabel label = (JLabel) component;
				label.setFont(BOLD);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

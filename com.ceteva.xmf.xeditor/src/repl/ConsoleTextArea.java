package repl;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import console.Console;
import console.XMFEditor;

public class ConsoleTextArea extends JTextArea implements MouseListener {

	public static String getClipBoard() {
		try {
			return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private TextAction PASTE = new TextAction("Paste") {

		@Override
		public void actionPerformed(ActionEvent e) {
			insert(getClipBoard());
		}

	};

	private TextAction COPY = new TextAction("Copy") {

		private DefaultEditorKit.CopyAction action = new DefaultEditorKit.CopyAction();

		@Override
		public void actionPerformed(ActionEvent e) {
			action.actionPerformed(e);
		}
	};

	private ConsoleInput   input;
	private String         buffer       = "";
	private int            bufferIndex  = 0;
	private int            commandIndex = 0;
	private InputMode      mode         = InputMode.NORMAL;
	private NameResolution names        = null;
	private JPopupMenu     popup        = new JPopupMenu();
	private Action[]       textActions  = { COPY, PASTE };

	public ConsoleTextArea(ConsoleInput input, int rows, int cols) {
		this.input = input;
		setEditable(false);
		setFont(new Font("Monospaced", Font.PLAIN, 14));
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (e.getOppositeComponent() == names) {
					requestFocus();
				}
			}
		});
		addMouseListener(this);
		for (Action textAction : textActions) {
			popup.add(new JMenuItem(textAction));
		}
		String osName = System.getProperties().getProperty("os.name");
		if (osName.startsWith("Mac OS X")) {
			Keymap km = getKeymap();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK);
			km.addActionForKeyStroke(ks, COPY);
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK);
			km.addActionForKeyStroke(ks, PASTE);
		}
	}

	public void replaceBuffer(String newBuffer) {
		String text = getText().substring(0, getText().length() - buffer.length());
		buffer = newBuffer;
		setText(text + newBuffer);
	}

	public void addChar(char c) {
		switch (c) {
		case '\b':
			deleteChar();
			break;
		case '\n':
			newline();
			break;
		default:
			insertChar(c);
		}
	}

	private void newline() {
		for (char out : buffer.toCharArray()) {
			input.add(out);
		}
		input.add('\n');
		commandIndex = 0;
		bufferIndex  = 0;
		setText(getText().substring(0, getText().length()) + "\n");
		buffer = "";
		mode   = InputMode.NORMAL;
		if (names != null) {
			names.dispose();
			names = null;
			requestFocus();
		}
	}

	private void insertChar(char c) {
		int length = buffer.length();
		String pre = buffer.substring(0, bufferIndex);
		String post = buffer.substring(bufferIndex, length);
		replaceBuffer(pre + c + post);
		bufferIndex++;
		switch (mode) {
		case NORMAL:
			break;
		case DOT:
			if (names != null) {
				names.insertChar(c);
				if (names.isSingleton()) {
					String lastName = names.getLastName();
					String prefix = names.getPrefix();
					resetNames();
					insert(lastName.replaceFirst(prefix, ""));
				} else if (names.hasCommonPrefix()) {
					String commonPrefix = names.getCommonPrefix();
					String prefix = names.getPrefix();
					insert(commonPrefix.replaceFirst(prefix, ""));
				}
			}
			break;
		}
	}

	public char currentChar() {
		return buffer.charAt(bufferIndex - 1);
	}

	public void deleteChar() {
		// Remove the character at the buffer index and reset the text...
		int length = buffer.length();
		if (length > 0) {
			if (bufferIndex > 0) {
				String pre = buffer.substring(0, bufferIndex - 1);
				String post = (bufferIndex == length) ? "" : buffer.substring(bufferIndex, length);
				replaceBuffer(pre + post);
				bufferIndex--;
			}
		}
		switch (mode) {
		case NORMAL:
			break;
		case DOT:
			names.deleteChar();
			break;
		}
	}

	public void previousCommand() {
		Console.call("getCommand", new Object[] { commandIndex }, (command) -> {
			if (command != "") {
				setLine((String) command + ";");
				commandIndex++;
			}
		});
		scrollToEnd();
	}

	public void setLine(String command) {
		replaceBuffer(command);
		bufferIndex = buffer.length();
	}

	public void nextCommand() {
		if (commandIndex > 1) {
			Console.call("getCommand", new Object[] { commandIndex - 2 }, (command) -> {
				if (command != "") {
					setLine((String) command + ";");
					commandIndex--;
				}
			});
		}
		scrollToEnd();
	}

	public void left() {
		if (bufferIndex > 0)
			bufferIndex--;
		scrollToEnd();
	}

	public void right() {
		if (bufferIndex < buffer.length())
			bufferIndex++;
		scrollToEnd();
	}

	void scrollToEnd() {
		setCaretPosition(getText().length());
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int length = getText().length() - (buffer.length() - bufferIndex);
		try {
			Rectangle2D r = modelToView(length);
			if (r != null)
				drawCaret(g, (int) r.getX(), (int) r.getY());
		} catch (BadLocationException e) {
			System.err.println(e);
		}
	}

	public static Shape drawTriangle(Graphics2D graphics, double x, double y, double height, double width) {
		final GeneralPath triangle = new GeneralPath();
		triangle.setWindingRule(Path2D.WIND_EVEN_ODD);
		triangle.moveTo(x, y);
		triangle.lineTo(x + (width / 2.0), y - height);
		triangle.lineTo(x - (width / 2.0), y - height);
		triangle.lineTo(x, y);
		triangle.closePath();
		graphics.fill(triangle);
		return triangle;
	}

	private void drawCaret(Graphics g, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		drawTriangle((Graphics2D) g, x, y + 2, 5, 5);
	}

	public void insert(String text) {
		for (int i = 0; i < text.length(); i++) {
			insertChar(text.charAt(i));
		}
	}

	public String getBuffer() {
		return buffer;
	}

	public void handleNameResolution(Vector<String> names) {
		SwingUtilities.invokeLater(() -> {
			int length = getText().length();
			try {
				Rectangle2D r = modelToView(length);
				Point p = new Point((int) r.getX(), (int) r.getY());
				SwingUtilities.convertPointToScreen(p, this);
				this.names = new NameResolution(p.x, p.y, names, this);
			} catch (BadLocationException e) {
				System.err.println(e);
			}
		});

	}

	public void period() {
		mode = InputMode.DOT;
	}

	public void resetNames() {
		if (names != null) {
			names.dispose();
			this.names = null;
			mode       = InputMode.NORMAL;
		}
		grabFocus();
	}

	public void doubleClick(String text) {
		String prefix = names.getPrefix();
		resetNames();
		for (int i = 0; i < prefix.length(); i++) {
			deleteChar();
		}
		insert(text);
	}

	public char previousChar() {
		int index = getCaretPosition() - 2;
		if (index > getText().length()) {
			return getText().charAt(index);
		} else
			return '\0';
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!maybeShowPopup(e)) {
			int length = getText().length();
			try {
				Rectangle2D r = modelToView(length - (buffer.length() - bufferIndex));
				FontMetrics metric = getFontMetrics(getFont());
				int mousex = e.getX();
				int caretx = (int) r.getX();
				if (mousex < caretx) {
					while (mousex < caretx) {
						left();
						caretx -= metric.stringWidth("x");
					}
				} else {
					while (mousex > caretx) {
						right();
						caretx += metric.stringWidth("x");
					}
				}
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
			return true;
		} else
			return false;
	}
}

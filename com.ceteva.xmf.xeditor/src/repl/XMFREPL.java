package repl;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice.Level;

import clients.ClientResult;
import console.Console;
import editor.XMFParserNotice;
import threads.ThreadInitiator;
import xos.OperatingSystem;

public class XMFREPL extends JPanel implements KeyListener {

	public static int getLine(int index, String text) {
		int line = 0;
		for (int i = 0; i < index; i++) {
			if (text.charAt(i) == '\n')
				line++;
		}
		return line;
	}

	public static int getOffset(int index, String text) {
		// Get to start of token. Skip back to end of token, then skip to start.
		while (index >= 0 && (index >= text.length() || text.charAt(index) != ' '))
			index--;
		while (index >= 0 && (index >= text.length() || text.charAt(index) != ' '))
			index--;
		return index + 1;
	}

	public static int getTokenLength(int index, String text) {
		int length = 0;
		while (index >= 0 && (index < text.length() && text.charAt(index) != ' ')) {
			index++;
			length++;
		}
		return length;
	}

	private Console         console;
	private ConsoleTextArea editor;
	private char            previousChar;

	public XMFREPL(Console console, ConsoleTextArea editor) {
		this.console = console;
		this.editor  = editor;
		setLayout(new BorderLayout());
		add(new JScrollPane(editor), BorderLayout.CENTER);
		editor.addKeyListener(this);
	}

	public void compileAndLoad(File file) {
		Console.call("compileAndLoad", new Object[] { file.getAbsolutePath() ,Console.getLanguageName(file)}, (ok) -> System.out.println("[ loaded " + file + "] " + ok));
	}

	public void compileAndLoad(String text, String language) {
		Console.call("compileAndLoadText", new Object[] { text, language }, (ok) -> System.out.println("[ loaded text ] " + ok));
	}

	public void compileAndLoadManifest(File file) {
		Console.call("compileAndLoadManifest", new Object[] { file.getAbsolutePath() }, (ok) -> System.out.println("[ loaded " + file + "] " + ok));
	}

	public void eval(String text) {
		Console.send("eval", new Object[] { text });
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			editor.previousCommand();
			e.consume();
			break;
		case KeyEvent.VK_DOWN:
			editor.nextCommand();
			e.consume();
			break;
		case KeyEvent.VK_LEFT:
			editor.left();
			break;
		case KeyEvent.VK_RIGHT:
			editor.right();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		editor.addChar(e.getKeyChar());
		if (e.getKeyChar() == '.') {
			e.consume();
			period();
		} else if (e.getKeyChar() == ':' && previousChar == ':') {
			e.consume();
			colonColon();
		}
		previousChar = e.getKeyChar();
	}

	private void colonColon() {
		try {
			Rectangle r;
			r = editor.modelToView(editor.getCaretPosition());
			Point pt = new Point(r.x, r.y);
			SwingUtilities.convertPointToScreen(pt, editor);
			editor.period();
			Console.send("topLevelColonColon", new Object[] { this, editor.getBuffer(), pt.x, pt.y });
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public ParseResult parse(Parser parser, String text) {

		int[] line = new int[] { -1 };
		int[] offset = new int[] { -1 };
		int[] tokenLength = new int[] { 1 };
		String[] message = new String[] { "" };

		Console.call("parser", new Object[] { text, "OCL::OCL" }, (value) -> {
			if (value instanceof Vector) {
				Vector<Object> result = (Vector<Object>) value;
				int charStart = (int) result.get(0);
				int charEnd = (int) result.get(1);
				message[0] = (String) result.get(2);
				if (charStart >= 0) {
					if (charStart == charEnd) {
						line[0]        = getLine(charStart, text) + 1;
						offset[0]      = getOffset(charStart - 1, text);
						tokenLength[0] = getTokenLength(offset[0], text);
					} else {
						line[0]        = getLine(charStart, text) + 1;
						offset[0]      = getOffset(charStart - 1, text);
						tokenLength[0] = charEnd - charStart;
					}
				}
			}
		});

		return new ParseResult() {

			@Override
			public Exception getError() {
				return null;
			}

			@Override
			public int getFirstLineParsed() {
				return 0;
			}

			@Override
			public int getLastLineParsed() {
				return 0;
			}

			@Override
			public List<ParserNotice> getNotices() {
				Vector<ParserNotice> notices = new Vector<>();
				if (offset[0] >= 0) {
					XMFParserNotice notice = new XMFParserNotice(parser, line[0], offset[0], message[0], tokenLength[0], Level.ERROR);
					System.err.println(notice);
					notices.add(notice);
				}

				return notices;
			}

			@Override
			public Parser getParser() {
				return parser;
			}

			@Override
			public long getParseTime() {
				return 0;
			}
		};
	}

	public void setDotExtension(String name) {
		while (editor.currentChar() != '.')
			editor.deleteChar();
		editor.right();
		editor.insert(name);
		editor.scrollToEnd();
	}

	private void period() {
		try {
			Rectangle r;
			r = editor.modelToView(editor.getCaretPosition());
			Point pt = new Point(r.x, r.y);
			SwingUtilities.convertPointToScreen(pt, editor);
			editor.period();
			Console.send("topLevelPeriod", new Object[] { this, editor.getBuffer(), pt.x, pt.y });
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void handleNameResolution(Vector<String> names) {
		editor.handleNameResolution(names);
	}

}

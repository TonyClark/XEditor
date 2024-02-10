package console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rtextarea.RTextScrollPane;

import editor.Definiens;
import editor.EditorTextArea;
import editor.StatusBar;
import editor.construct.LanguageConstruct;
import files.DefiniensNode;
import files.FileNode;
import qwerky.tools.filesearch.SearchHit;
import qwerky.tools.filesearch.SearchResponse;

public class XMFEditor extends JPanel implements DocumentListener, HyperlinkListener {

	private static final int PUSH_DEFINIENS       = 0;
	private static final int POP_DEFINIENS        = 1;
	private static final int REGISTER_LOCATION    = 2;
	private static final int REGISTER_DEFINIENDUM = 3;
	private static final int PUSH_DEFINIENS_REF   = 4;

	private static final Color SEARCH_HIGHLIGHT_COLOUR = new Color(0, 0, 100, 50);

	private File                                file;
	private EditorTextArea                      editor;
	private Console                             console;
	private Stack<Definiens>                    definiens    = new Stack<Definiens>();
	private Hashtable<Definiens, DefiniensNode> definiensMap = new Hashtable<Definiens, DefiniensNode>();
	private StatusBar                           statusBar;
	private ErrorStrip                          errorStrip;
	private XMFEditor                           previous, next;

	public XMFEditor(Console console) {
		setLayout(new BorderLayout());
		statusBar = new StatusBar();
		editor    = new EditorTextArea(console, 500, 500, "text/XOCL", statusBar);
		JScrollPane scroll = new RTextScrollPane(editor);
		editor.setScrollPane(scroll);
		editor.getDocument().addDocumentListener(this);
		editor.addHyperlinkListener(this);
		add(scroll, BorderLayout.CENTER);
		scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		errorStrip = new ErrorStrip(editor);
		add(errorStrip, BorderLayout.LINE_END);
		add(statusBar, BorderLayout.SOUTH);
		this.console  = console;
		this.previous = this;
		this.next     = this;
	}

	public void addLanguageMenuItems(String language) {
		String[] menuItemNames = new String[] { "SPAM" };
		for (String item : menuItemNames) {
			editor.addLanguageMenuItem(item);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		setDirty(true);
	}

	private void clearDefinitions() {
		if (file != null) {
			console.getXmfPanel().getFileSystem().clear(file);
			definiensMap.clear();
			definiens.clear();
		}
	}

	public void displayError(String message) {
		statusBar.displayError(message);
		console.hasError(this, true);
	}

	public void endActivity(String name) {
		statusBar.endActivity(name, System.currentTimeMillis());
	}

	public XMFEditor findEditor(File file) {
		if (file.equals(this.file))
			return this;
		else if (next == null)
			return null;
		else
			return next.findEditor(file);
	}

	public Console getConsole() {
		return console;
	}

	public EditorTextArea getEditor() {
		return editor;
	}

	public File getFile() {
		return file;
	}

	public XMFEditor getNext() {
		return next;
	}

	private Definiens getNonRef(Stack<Definiens> s) {
		if (s.isEmpty())
			return null;
		else if (s.peek().isRef()) {
			Definiens d = s.pop();
			Definiens nonref = getNonRef(s);
			s.push(d);
			return nonref;
		} else
			return s.peek();
	}

	public XMFEditor getPrevious() {
		return previous;
	}

	public void highlight(SearchResponse response) {
		for (SearchHit hit : response.getHits()) {
			try {
				editor.addLineHighlight(hit.getLine() - 1, SEARCH_HIGHLIGHT_COLOUR);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setDirty(true);
	}

	public void load(File file) {
		editor.load(file);
		setFile(file);
		setDirty(false);
	}

	public void missingDefiniens(String message) {
	}

	public Rectangle modelToView(int pos) {
		try {
			return editor.modelToView(pos);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void performAction(Vector<Object> action) {
		int a = (int) action.get(0);
		switch (a) {
		case PUSH_DEFINIENS:
			pushDefiniens((String) action.get(1), (String) action.get(2), (int) action.get(3), (int) action.get(4));
			break;
		case PUSH_DEFINIENS_REF:
			pushDefiniensRef((String) action.get(1), (String) action.get(2), (int) action.get(3), (int) action.get(4));
			break;
		case POP_DEFINIENS:
			popDefiniens();
			break;
		case REGISTER_LOCATION:
			registerLocation((int) action.get(1), (int) action.get(2));
			break;
		case REGISTER_DEFINIENDUM:
			registerDefiniendum((String) action.get(1), (int) action.get(2), (int) action.get(3), (int) action.get(4), (int) action.get(5));
			break;
		default:
			throw new Error("unknown action: " + a);
		}
	}

	public synchronized void performActions(Vector<Object> actions) {
		new Thread(() -> {
			try {
				clearDefinitions();
				for (int i = actions.size() - 1; i >= 0; i--) {
					performAction((Vector<Object>) actions.get(i));
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}).start();
	}

	public void popDefiniens() {
		definiens.pop();
	}

	public void pushDefiniens(String icon, String name, int charStart, int charEnd) {
		Definiens def = new Definiens(name, charStart, charEnd, file, false);
		DefiniensNode node = new DefiniensNode(def, icon, console, console.getXmfPanel().getFileSystem().getFileSystem());
		definiensMap.put(def, node);
		Definiens d = getNonRef(definiens);
		if (d == null) {
			console.getXmfPanel().getFileSystem().getFileSystem().addDefiniens(node);
		} else {
			DefiniensNode parent = definiensMap.get(d);
			console.getXmfPanel().getFileSystem().getFileSystem().addDefiniens(node, parent);
		}
		definiens.push(def);
	}

	public void pushDefiniensRef(String icon, String name, int charStart, int charEnd) {
		Definiens def = new Definiens(name, charStart, charEnd, file, true);
		definiens.push(def);
	}

	public void registerDefiniendum(String name, int start, int end, int defStart, int defEnd) {
		// System.err.println("REGISTER " + name + " " + defStart + " " + defEnd);
		editor.registerDefiniendum(name, start, end, defStart, defEnd);
	}

	public void registerLocation(int start, int end) {
		editor.registerLocation(start, end);
	}

	public void registerResource(String name, int start, int end, String file) {
		JMenuItem item = new JMenuItem("Edit Definition");
		item.addActionListener((e) -> editor.load(new File(file)));
		editor.registerResource(name, start, end, item);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		setDirty(true);
	}

	public void reset() {
		editor.reset();
		definiens.clear();
		definiensMap.clear();
	}

	public void saveCaretPosition(FileNode node) {
		node.setCaretPosition(editor.getCaretPosition());
	}

	public void setCaretPosition(FileNode node) {
		editor.setCaretPosition(node.getCaretPosition());
	}

	public void setCaretPosition(int pos) {
		if (pos < editor.getText().length()) {
			java.awt.geom.Rectangle2D view = modelToView(pos); // View where pos is visible
			scrollRectToVisible(view.getBounds()); // Scroll to the rectangle provided by view
			editor.setCaretPosition(pos);
		}
	}

	public void setLinePosition(int line) {
		int pos = 0;
		String text = editor.getText();
		while(pos < text.length() &&  line > 1) {
			if(text.charAt(pos) == '\n') line--;
			pos++;
		}
		setCaretPosition(pos);
	}

	public void setDirty(boolean dirty) {
		console.setDirty(this, dirty);
	}

	public void setNext(XMFEditor next) {
		this.next = next;
	}

	public void setPrevious(XMFEditor previous) {
		this.previous = previous;
	}

	public void startActivity(String name) {
		statusBar.startActivity(name, System.currentTimeMillis());
	}

	public String toString() {
		return "Editor(" + file.getName() + ")";
	}

	public void unboundVar(String name, int start, int end) {
		editor.unboundVar(name, start, end);
	}

	public void updateErrorStrip() {
		editor.updateErrorStrip();
	}

	public void setLanguageConstructs(Vector<LanguageConstruct> languageConstructs) {
		editor.setLanguageConstructs(languageConstructs);
	}

	public void setFile(File file) {
		this.file = file;
	}

}

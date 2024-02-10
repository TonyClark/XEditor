package console;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice.Level;

import editor.XMFParserNotice;
import exceptions.ExceptionNode;
import exceptions.ExceptionPanel;
import files.FileNode;
import qwerky.tools.filesearch.SearchResponse;
import repl.ConsoleTextArea;
import repl.XMFREPL;
import tabs.TabLabel;
import web.BrowserPanel;

public class SourcePanel extends JSplitPane implements MouseMotionListener {

	private static interface Action {
		void action();
	}

	private static XMFEditorList  editors;
	private static XMFREPL        repl;
	private static ExceptionPanel exceptions = null;
	private static JTabbedPane    tabs       = new JTabbedPane();
	private static JSplitPane     replExceptions;
	private static int            loc        = Console.CONSOLE.getWidth();

	public SourcePanel(Console console, ConsoleTextArea editor) {
		super(JSplitPane.VERTICAL_SPLIT);
		replExceptions = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		editors        = new XMFEditorList(console);
		repl           = new XMFREPL(console, editor);
		replExceptions.setLeftComponent(repl);
		setTopComponent(tabs);
		addTab(editors.getCurrent());
		browseHome();
		setBottomComponent(replExceptions);
		setDividerLocation(Console.CONSOLE_HEIGHT - 300);
		SplitPaneUI spui = replExceptions.getUI();
		if (spui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI) spui).getDivider().addMouseMotionListener(this);
		}
	}

	public void browseHome() {
		createBrowser("XEditor", (webBrowser) -> webBrowser.loadFile("doc/xeditor.html"));
	}

	public void addLanguageMenuItems(String language) {
		editors.getCurrent().addLanguageMenuItems(language);
	}

	public int indexOfEditor(XMFEditor editor) {
		return tabs.indexOfComponent(editor);
	}

	public int indexOfBrowser(String label) {
		return tabs.indexOfTab(label);
	}

	public void addTab(XMFEditor editor) {
		if (indexOfEditor(editor) == -1) {
			tabs.addTab(editor.getFile().getName(), editor);
			TabLabel.LabelAction delete = () -> tabs.remove(indexOfEditor(editor));
			TabLabel.LabelAction select = () -> select(editor);
			String name = editor.getFile().getName();
			TabLabel label = new TabLabel(name, name, delete, select);
			label.setBackgroundToolTipText(editor.getFile().getAbsolutePath());
			tabs.setTabComponentAt(indexOfEditor(editor), label);
			tabs.setSelectedIndex(indexOfEditor(editor));
		} else {
			tabs.setSelectedIndex(indexOfEditor(editor));
		}
	}

	private void select(XMFEditor editor) {
		tabs.setSelectedComponent(editor);
	}

	private void select(BrowserPanel browser) {
		tabs.setSelectedComponent(browser);
	}

	public void back() {
		editors.back();
		int divider = getDividerLocation();
		addTab(editors.getCurrent());
		setDividerLocation(divider);
	}

	public void compileAndLoad(File file) {
		repl.compileAndLoad(file);
	}

	public void compileAndLoad(String text, String language) {
		repl.compileAndLoad(text, language);
	}

	public void compileAndLoadManifest(File file) {
		repl.compileAndLoadManifest(file);
	}

	public void edit(SearchResponse response) {
		load(response.getFile());
		editors.getCurrent().highlight(response);
	}

	public void eval(String text) {
		repl.eval(text);
	}

	public void forward() {
		editors.forward();
		int divider = getDividerLocation();
		addTab(editors.getCurrent());
		setDividerLocation(divider);
	}

	public File getFile() {
		return editors.getCurrent().getFile();
	}

	public Vector<File> getNextFiles() {
		return editors.getNextFiles();
	}

	public Vector<File> getPreviousFiles() {
		return editors.getPreviousFiles();
	}

	public void load(File file) {
		editors.makeCurrent(file);
		int divider = getDividerLocation();
		addTab(editors.getCurrent());
		setDividerLocation(divider);
	}

	public ParseResult parse(File file, Parser parser, String text, String languageName) {
		int[] line = new int[] { -1 };
		int[] offset = new int[] { -1 };
		int[] tokenLength = new int[] { 1 };
		String[] message = new String[] { "" };
		Console.call("parser", new Object[] { file.getAbsoluteFile().toString(), text, languageName, editors.getCurrent() }, (value) -> {
			if (value instanceof Vector) {
				Vector<Object> result = (Vector<Object>) value;
				int charStart = (int) result.get(0);
				int charEnd = (int) result.get(1);
				message[0] = (String) result.get(2);
				if (charStart >= 0) {
					if (charStart == charEnd) {
						line[0]        = XMFREPL.getLine(charStart, text) - 1;
						offset[0]      = XMFREPL.getOffset(charStart - 1, text);
						tokenLength[0] = XMFREPL.getTokenLength(offset[0], text);
					} else {
						line[0]        = XMFREPL.getLine(charStart, text) + 1;
						offset[0]      = charStart;
						tokenLength[0] = charEnd - charStart;
					}
				}
			}
		});
		return new ParseResult() {

			@Override
			public Exception getError() { // TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getFirstLineParsed() { // TODO Auto-generated method
				return 0;
			}

			@Override
			public int getLastLineParsed() { // TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<ParserNotice> getNotices() {
				Vector<ParserNotice> notices = new Vector<>();
				if (offset[0] >= 0) {
					XMFParserNotice notice = new XMFParserNotice(parser, line[0], offset[0], message[0], tokenLength[0], Level.ERROR);
					notices.add(notice);
				}

				return notices;
			}

			@Override
			public Parser getParser() { // TODO Auto-generated method stub
				return parser;
			}

			@Override
			public long getParseTime() { // TODO Auto-generated method stub
				return 0;
			}
		};
	}

	public void registerLocations(Parser parser, String text, String languageName) {
		Console.send("registerLocations", new Object[] { text, languageName, editors.getCurrent() });
	}

	public void saveCaretPosition(FileNode node) {
		editors.getCurrent().saveCaretPosition(node);
	}

	public void setCaretPosition(FileNode node) {
		editors.getCurrent().setCaretPosition(node);
		editors.getCurrent().scrollRectToVisible(editors.getCurrent().modelToView(node.getCaretPosition()));
	}

	public void setCaretPosition(int pos) {
		editors.getCurrent().setCaretPosition(pos);
	}

	public void setLinePosition(int line) {
		editors.getCurrent().setLinePosition(line);
	}

	public void handleNameResolution(Vector<String> names) {
		repl.handleNameResolution(names);
	}

	public void setDirty(XMFEditor editor, boolean dirty) {
		int index = indexOfEditor(editor);
		if (index >= 0) {
			TabLabel label = (TabLabel) tabs.getTabComponentAt(index);
			String name = label.getText();
			if (name.startsWith("*"))
				name = name.substring(1);
			name = (dirty ? "*" : "") + name;
			label.setText(name);
			label.resetBackground();
		}
	}

	public void setDirty(File file, boolean dirty) {
		int index = indexOfEditor(file);
		setDirty((XMFEditor) tabs.getComponentAt(index), dirty);
	}

	private int indexOfEditor(File file) {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			Component component = tabs.getComponentAt(i);
			if (component instanceof XMFEditor) {
				XMFEditor editor = (XMFEditor) component;
				if (editor.getFile().equals(file))
					return i;
			}
		}
		return -1;
	}

	public void hasError(XMFEditor editor, boolean hasError) {
		int index = indexOfEditor(editor);
		if (index >= 0) {
			if (hasError)
				tabs.setBackgroundAt(index, Color.red);
			else
				tabs.setBackgroundAt(index, Color.white);
		}
	}

	public void hasError(File file, boolean hasError) {
		int index = indexOfEditor(file);
		if (index >= 0) {
			if (hasError)
				tabs.setBackgroundAt(index, Color.red);
			else
				tabs.setBackgroundAt(index, Color.white);
		}
	}

	public BrowserPanel createBrowser(String label, Consumer<BrowserPanel> init) {
		int index = indexOfBrowser(label);
		if (index == -1) {
			BrowserPanel browser = new BrowserPanel(init);
			tabs.addTab(label, browser);
			TabLabel.LabelAction delete = () -> {
				tabs.remove(indexOfBrowser(label));
			};
			TabLabel.LabelAction select = () -> select(browser);
			TabLabel lab = new TabLabel(label, label, delete, select);
			tabs.setTabComponentAt(indexOfBrowser(label), lab);
			index = indexOfBrowser(label);
		}
		tabs.setSelectedIndex(index);
		return (BrowserPanel) tabs.getSelectedComponent();
	}

	public void showException(ExceptionNode exception) {
		if (exceptions == null) {
			exceptions = new ExceptionPanel();
			replExceptions.setRightComponent(exceptions);
			loc = Console.CONSOLE.getWidth() / 2;
		}
		exceptions.setRoot(exception);
		replExceptions.setDividerLocation(loc);
		replExceptions.setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));
	}

	public void hideException() {
		replExceptions.setDividerLocation(Console.CONSOLE.getWidth());
		replExceptions.setDividerSize(0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) { 
		loc = replExceptions.getDividerLocation();
	}

}

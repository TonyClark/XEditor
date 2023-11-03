package console;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JSplitPane;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice.Level;

import editor.XMFParserNotice;
import files.FileNode;
import qwerky.tools.filesearch.SearchResponse;
import repl.ConsoleTextArea;
import repl.XMFREPL;

public class SourcePanel extends JSplitPane {

	private static XMFEditorList editors;
	private static XMFREPL       repl;

	public SourcePanel(Console console,ConsoleTextArea editor) {
		super(JSplitPane.VERTICAL_SPLIT);
		editors = new XMFEditorList(console);
		repl = new XMFREPL(console,editor);
		setTopComponent(editors.getCurrent());
		setBottomComponent(repl);
		setDividerLocation(Console.CONSOLE_HEIGHT - 300);
	}

	public void addLanguageMenuItems(String language) {
		editors.getCurrent().addLanguageMenuItems(language);
	}

	public void back() {
		editors.back();
		int divider = getDividerLocation();
		setTopComponent(editors.getCurrent());
		setDividerLocation(divider);
	}

	public void compileAndLoad(File file, String language) {
		repl.compileAndLoad(file, language);
	}

	public void compileAndLoad(String text,String language) {
		repl.compileAndLoad(text,language);
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
		setTopComponent(editors.getCurrent());
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
		setTopComponent(editors.getCurrent());
		setDividerLocation(divider);
	}

	public ParseResult parse(File file, Parser parser, String text, String languageName) {
		int[]    line        = new int[] { -1 };
		int[]    offset      = new int[] { -1 };
		int[]    tokenLength = new int[] { 1 };
		String[] message     = new String[] { "" };
		Console.call("parser", new Object[] { file.getAbsoluteFile().toString(), 
				text, languageName, editors.getCurrent() }, (value) -> {
			if (value instanceof Vector) {
				Vector<Object> result    = (Vector<Object>) value;
				int            charStart = (int) result.get(0);
				int            charEnd   = (int) result.get(1);
				message[0] = (String) result.get(2);
				if (charStart >= 0) {
					if (charStart == charEnd) {
						line[0] = XMFREPL.getLine(charStart, text) - 1;
						offset[0] = XMFREPL.getOffset(charStart - 1, text);
						tokenLength[0] = XMFREPL.getTokenLength(offset[0], text);
					} else {
						line[0] = XMFREPL.getLine(charStart, text) + 1;
						offset[0] = charStart;
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

	public void handleNameResolution(Vector<String> names) {
		repl.handleNameResolution(names);
	}

}

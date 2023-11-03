package console;

import java.io.File;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

import console.browser.Browser;
import files.FileNode;
import files.FileSystemWidget;
import qwerky.tools.filesearch.SearchResponse;
import repl.ConsoleTextArea;

public class XMFPanel extends JSplitPane {

	private static Browser     browser;
	private static SourcePanel sourcePanel;

	public static FileSystemWidget getFileSystem() {
		return browser.getFileSystem();
	}

	public static SourcePanel getSourcePanel() {
		return sourcePanel;
	}

	public XMFPanel(Console console, ConsoleTextArea editor, String roots) {
		super(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(browser = new Browser(console, roots)), sourcePanel = new SourcePanel(console, editor));
		setDividerLocation(200);
	}

	public void addLanguageMenuItems(String language) {
		sourcePanel.addLanguageMenuItems(language);
	}

	public void back() {
		sourcePanel.back();
		File file = sourcePanel.getFile();
		if (file != null)
			browser.getFileSystem().select(file);
	}

	public void compileAndLoad(File file, String language) {
		sourcePanel.compileAndLoad(file, language);
	}

	public void compileAndLoad(String text, String language) {
		sourcePanel.compileAndLoad(text, language);
	}

	public void compileAndLoadManifest(File file) {
		sourcePanel.compileAndLoadManifest(file);
	}

	public void edit(SearchResponse response) {
		sourcePanel.edit(response);
	}

	public void eval(String text) {
		sourcePanel.eval(text);
	}

	public void forward() {
		sourcePanel.forward();
		File file = sourcePanel.getFile();
		if (file != null)
			browser.getFileSystem().select(file);
	}

	public Vector<File> getNextFiles() {
		return sourcePanel.getNextFiles();
	}

	public Vector<File> getPreviousFiles() {
		return sourcePanel.getPreviousFiles();
	}

	public void load(File file) {
		sourcePanel.load(file);
	}

	public ParseResult parse(File file, Parser parser, String text, String languageName) {
		return sourcePanel.parse(file, parser, text, languageName);
	}

	public void registerLocations(Parser parser, String text, String languageName) {
		sourcePanel.registerLocations(parser, text, languageName);
	}

	public void saveCaretPosition(FileNode node) {
		sourcePanel.saveCaretPosition(node);
	}

	public void setCaretPosition(FileNode selectedFile) {
		sourcePanel.setCaretPosition(selectedFile);
	}

	public void setCaretPosition(int pos) {
		sourcePanel.setCaretPosition(pos);
	}

	public void addChild(int parent, int child, String label) {
		browser.addChild(parent, child, label);
	}

	public void setRoot(Vector<Object> tree) {
		browser.setRoot(tree);
	}

	public void setBrowserElementImage(int id, String imageFile) {
		browser.setElementImage(id, imageFile);
	}

	public void refreshBrowserNode(int id) {
		browser.refreshNode(id);
	}

	public void setBrowserHoverText(int id, String text) {
		browser.setHoverText(id, text);
	}

	public void browseDir(File dir) {
		browser.browseDir(dir);
	}

	public void handleNameResolution(Vector<String> names) {
		sourcePanel.handleNameResolution(names);
	}

}

package console;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.HyperlinkEvent;

import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class XMFEditorList {

	private Console                    console;
	private XMFEditor                  start, current;
	private Hashtable<File, XMFEditor> cache = new Hashtable<File, XMFEditor>();

	public XMFEditorList(Console console) {
		this.console = console;
		current = newEditor(new File("xmf/scratch.xmf"));
		current.getEditor().setLinkGenerator(new DocumentationLinkGenerator());
		current.getEditor().setReadOnly(true);
		current.getEditor().setLineWrap(true);
		start = current;
	}

	public String toString() {
		String    s = "[" + start.toString();
		XMFEditor e = start;
		while (e.getNext() != e) {
			String isCurrent = e.getNext() == current ? "*" : "";
			s = s + "," + isCurrent + e.getNext();
			e = e.getNext();
		}
		return s + "]";
	}

	public XMFEditor getCurrent() {
		return current;
	}

	public void makeCurrent(File file) {
		if (!current.getFile().equals(file)) {
			XMFEditor editor = cache.containsKey(file) ? unlink(cache.get(file)) : newEditor(file);
			editor.setPrevious(current);
			editor.setNext(editor);
			current.setNext(editor);
			current = editor;
		}
	}

	private XMFEditor unlink(XMFEditor e) {
		XMFEditor editor = start;
		while (editor.getNext() != editor) {
			if (editor.getFile().equals(e.getFile())) {
				editor.getPrevious().setNext(editor.getNext());
				editor.getNext().setPrevious(editor.getPrevious());
				break;
			} else
				editor = editor.getNext();
		}
		if (editor.getNext().getFile().equals(e.getFile())) {
			editor.getPrevious().setNext(editor.getPrevious());
		}
		e.setNext(e);
		e.setPrevious(e);
		return e;
	}

	private XMFEditor newEditor(File file) {
		XMFEditor editor = new XMFEditor(console);
		editor.load(file);
		cache.put(file, editor);
		return editor;
	}

	public void forward() {
		current = current.getNext();
	}

	public void back() {
		current = current.getPrevious();
	}

	public Vector<File> getPreviousFiles() {
		Vector<File> previous = new Vector<File>();
		XMFEditor    e        = current.getPrevious();
		while (e.getPrevious() != e) {
			previous.add(e.getFile());
			e = e.getPrevious();
		}
		return previous;
	}

	public Vector<File> getNextFiles() {
		Vector<File> next = new Vector<File>();
		XMFEditor    e    = current.getNext();
		while (e.getNext() != e) {
			next.add(e.getFile());
			e = e.getNext();
		}
		return next;
	}

}

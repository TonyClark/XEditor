package console;

import javax.swing.event.HyperlinkEvent;

public class DocumentationEditor extends XMFEditor {

	public DocumentationEditor(Console console) {
		super(console);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e instanceof GotoEvent) {
			GotoEvent g = (GotoEvent) e;
			if (g.getSectionNumber().equals("0.0")) {
				getEditor().setCaretPosition(0);
			} else {
				int i = getEditor().getText().lastIndexOf("§" + g.getSectionNumber());
				getEditor().setCaretPosition(i);
			}
		}
	}

	public String toString() {
		return "DocumentationEditor";
	}

}

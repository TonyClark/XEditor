package console;

import javax.swing.event.HyperlinkEvent;

import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class DocumentationLinkGenerator implements LinkGenerator {

	@Override
	public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea editor, int index) {
		if (isLink(editor.getText(), index)) {

			return new LinkGeneratorResult() {

				@Override
				public HyperlinkEvent execute() {
					String sectionNumber = getSectionNumber(editor, getLinkOffset(editor.getText(), index));
					return new GotoEvent(this, sectionNumber);
				}

				@Override
				public int getSourceOffset() {
					return getLinkOffset(editor.getText(), index);
				}
			};
		} else
			return null;
	}

	private boolean isLink(String text, int offset) {
		// A documentation link is starts with § and continues to the end of the line.
		return getLinkOffset(text, offset) != -1;
	}

	private int getLinkOffset(String text, int offset) {
		while (offset >= 0 && offset < text.length() && text.charAt(offset) != '§' && text.charAt(offset) != '\n') {
			offset--;
		}
		if (text.charAt(offset) == '§')
			return offset;
		else
			return -1;
	}

	protected String getSectionNumber(RSyntaxTextArea editor, int index) {
		String text = editor.getText();
		index += "§".length();
		String number = "";
		while (Character.isDigit(text.charAt(index)) || text.charAt(index) == '.') {
			number += text.charAt(index);
			index++;
		}
		return number;
	}

}

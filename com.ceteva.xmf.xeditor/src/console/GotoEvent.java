package console;

import javax.swing.event.HyperlinkEvent;

import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;

public class GotoEvent extends HyperlinkEvent {

	private String sectionNumber;

	public GotoEvent(LinkGeneratorResult result, String sectionNumber) {
		super(result, EventType.ACTIVATED, null);
		this.sectionNumber = sectionNumber;
	}

	public String getSectionNumber() {
		return sectionNumber;
	}

}

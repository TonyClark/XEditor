package editor;

import java.awt.Color;

import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

public class XMFParserNotice extends DefaultParserNotice {

	private static Color ERROR = new Color(204, 0, 0);
	private static Color WARNING = new Color(0, 0, 204);
	private static Color INFO = new Color(0, 153, 0);

	private Level level;

	public XMFParserNotice(Parser parser, int line, int offset, String message, int length, Level level) {
		super(parser,message,line,offset,length);
		this.level = level;
		setToolTipText(message);
	}

	@Override
	public Level getLevel() {
		return level;
	}

}

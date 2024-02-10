package editor;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.fife.ui.rsyntaxtextarea.TokenTypes;

public class XOCLTokens extends org.fife.ui.rsyntaxtextarea.modes.JavaTokenMaker {

	public static final Set<String> tokens = new HashSet<>();

	public XOCLTokens() {
		super();
	}

	@Override
	public void addToken(char[] segment, int start, int end, int tokenType, int startOffset, boolean hyperlink) {

		// This assumes all keywords, etc. were parsed as "identifiers."

		String s = new String(segment);
		String token = s.substring(start, Math.min(s.length(), end + 1));
		if (token.startsWith("//"))
			super.addToken(segment, start, end, tokenType, startOffset, hyperlink);
		else {
			if (token.length() > 0 && Character.isUpperCase(token.charAt(0))) {
				super.addToken(segment, start, end, TokenTypes.OPERATOR, startOffset, hyperlink);
			} else {
				if (token.startsWith("@")) {
					super.addToken(segment, start, start, TokenTypes.COMMENT_MULTILINE, startOffset, hyperlink);
					super.addToken(segment, start + 1, end, TokenTypes.OPERATOR, startOffset, hyperlink);
				} else {
					if (tokens.contains(token)) {
						super.addToken(segment, start, end, TokenTypes.RESERVED_WORD, startOffset, hyperlink);
					} else {
						if (token.length() > 0 && isIdentifier(token)) {
							super.addToken(segment, start, end, TokenTypes.IDENTIFIER, startOffset, hyperlink);
						} else
							super.addToken(segment, start, end, tokenType, startOffset, hyperlink);
					}
				}
			}
		}
	}

	private boolean isIdentifier(String token) {
		char c = token.charAt(0);
		return Character.isAlphabetic(c);
	}

	private Located getLocated(int pos) {
		return null;
	}

	public static void setTokens(Vector<String> tokenNames) {
		for (String token : tokenNames) {
			tokens.add(token);
		}
	}

}

package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class FileTokenizer implements Tokenizer {

	public static final int     EOF        = 1;
	public static final int     TERM       = 2;
	public static final int     FLOAT      = 4;
	public static final int     STRING     = 5;
	public static final int     NAME       = 6;
	public static final int     INT        = 7;
	public static final int     SPECIAL    = 8;
	public static final int     WHITESPACE = 9;
	public static final int     COMMENT    = 10;

	public static final boolean DEBUG      = true;

	public static void main(String[] args) {
		FileTokenizer t = new FileTokenizer("src/parser/test.xmf");
		System.out.println(t);
	}

	private String        fileName;
	private String        text;
	private Vector<Token> tokens             = new Vector<Token>();
	private int           tokenIndex         = 0;
	private int           tokenCharIndex     = 0;
	private int           lastTokenIndex     = 0;

	private int           lastTokenCharIndex = 0;

	public FileTokenizer(String fileName) {
		super();
		this.fileName = fileName;
		this.text = readFile();
		tokenize();
		//System.err.println(tokens);
	}

	public void addToken(Token token) {
		tokens.add(token);
	}

	@Override
	public String buffer() {
		return text;
	}

	@Override
	public int charCount() {
		int chars = 0;
		for (int i = 0; i < charPos(); i++) {
			int c = text.charAt(i);
			if (c == '\n')
				chars = 0;
			else
				chars++;
		}
		return chars;
	}

	@Override
	public int charPos() {
		return getCurrentToken().getCharStart() + getTokenCharIndex();
	}

	public void debug(int i, String message) {
		System.out.println(message);
	}

	private boolean endsMultiLineComment(int i) {
		return text.startsWith("*/", i);
	}

	private boolean endsSpecial(char c) {
		switch (c) {
		case '\"':
		case '(':
		case ')':
		case '{':
		case '}':
		case ';':
		case '\'':
		case ',':
			return true;
		}
		if (Character.isAlphabetic(c) || Character.isDigit(c))
			return true;
		if (Character.isWhitespace(c))
			return true;
		return false;
	}

	public Token getCurrentToken() {
		return tokens.get(tokenIndex);
	}

	public Token getLastToken() {
		return tokens.get(lastTokenIndex);
	}

	public int getLastTokenCharIndex() {
		return lastTokenCharIndex;
	}

	public int getLastTokenIndex() {
		return lastTokenIndex;
	}

	@Override
	public String getString(int charStart, int charEnd) {
		return text.substring(charStart, charEnd);
	}

	public int getTokenCharIndex() {
		return tokenCharIndex;
	}

	private boolean ignoreToken(Token token) {
		return token.getType() == WHITESPACE || token.getType() == COMMENT;
	}

	private boolean isNewLine(char c) {
		return c == '\n' || c == '\r';
	}

	private boolean isSeq(int i) {
		return text.startsWith("Seq{", i);
	}

	private boolean isSet(int i) {
		return text.startsWith("Set{", i);
	}

	private boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

	@Override
	public int lastCharPos() {
		return getLastToken().getCharStart() + getLastTokenCharIndex();
	}

	@Override
	public int lineCount() {
		return lines(charPos() - 1);
	}

	private int lines(int charPos) {
		int lines = 0;
		for (int i = 0; i < charPos; i++) {
			if (text.charAt(i) == '\n') {
				lines++;
			}
		}
		return lines;
	}

	@Override
	public void nextToken() {
		lastTokenIndex = tokenIndex++;
		lastTokenCharIndex = tokenCharIndex;
		while (tokenIndex < tokens.size() && ignoreToken(tokens.get(tokenIndex))) {
			tokenIndex++;
		}
		tokenCharIndex = 0;
	}

	@Override
	public boolean peek(String next) {
		return tokens.get(tokenIndex).getToken().startsWith(next);
	}

	@Override
	public int readChar() {
		Token token = getCurrentToken();
		while (tokenCharIndex == token.getToken().length())
			nextToken();
		int c = getCurrentToken().getToken().charAt(tokenCharIndex++);
		while (tokenCharIndex == token.getToken().length())
			nextToken();
		return c;
	}

	private String readFile() {
		try {
			return Files.readString(Path.of(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private int readMultiLineComment(int i) {
		String  token     = "";
		int     charStart = i;
		boolean seenEnd   = false;
		while (i < text.length() && !seenEnd) {
			if (endsMultiLineComment(i)) {
				seenEnd = true;
				token += "*/";
				i = i + "*/".length();
			} else
				token += text.charAt(i++);
		}
		if (seenEnd) {
			int charEnd = i - 1;
			addToken(new Token(token, COMMENT, charStart, charEnd));
			return i;
		} else
			throw new Error("EOF in multi-line comment.");
	}

	private int readName(int i) {
		int     charStart = i;
		String  token     = "";
		boolean isName    = true;
		while (i < text.length() && isName) {
			char c = text.charAt(i);
			if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
				token += c;
				i++;
			} else {
				isName = false;
				i--;
			}
		}
		int charEnd = i;
		addToken(new Token(token, NAME, charStart, charEnd));
		return i + 1;
	}

	private int readNumber(int i) {
		boolean isFloat      = false;
		String  number       = "";
		boolean isNumberChar = true;
		int     charStart    = i;
		while (i < text.length() && isNumberChar) {
			char c = text.charAt(i);
			if (Character.isDigit(c)) {
				number += c;
				i++;
			} else if (c == '.') {
				isFloat = true;
				number += c;
				i++;
			} else {
				isNumberChar = false;
				i--;
			}
		}
		int charEnd = i;
		if (isFloat) {
			addToken(new Token(number, FLOAT, charStart, charEnd));
		} else
			addToken(new Token(number, INT, charStart, charEnd));
		return i + 1;
	}

	private int readSeq(int i) {
		addToken(new Token("Seq{", SPECIAL, i, i + "Seq{".length()));
		return i + 4;
	}

	private int readSet(int i) {
		addToken(new Token("Set{", SPECIAL, i, i + "Set{".length()));
		return i + 4;
	}

	private int readSingleLineComment(int i) {
		String  token           = "";
		int     charStart       = i;
		boolean commentFinished = false;
		while (i < text.length() && !commentFinished) {
			char c = text.charAt(i);
			if (isNewLine(c)) {
				commentFinished = true;
				i--;
			} else {
				token += c;
				i++;
			}
		}
		int charEnd = i;
		addToken(new Token(token, COMMENT, charStart, charEnd));
		return i + 1;
	}

	private int readSpecial(int i) {
		String  token     = "" + text.charAt(i);
		int     charStart = i++;
		boolean isSpecial = true;
		while (i < text.length() && isSpecial) {
			char c = text.charAt(i);
			if (!endsSpecial(c)) {
				token += c;
				i++;
			} else {
				i--;
				isSpecial = false;
			}
		}
		int charEnd = i;
		addToken(new Token(token, SPECIAL, charStart, charEnd));
		return i + 1;
	}

	private int readString(int i) {
		String  token     = "";
		int     charStart = i;
		boolean isString  = true;
		i++;
		while (i < text.length() && isString) {
			char c = text.charAt(i++);
			if (c == '\"') {
				isString = false;
			} else
				token += c;
		}
		if (isString) {
			throw new Error("EOF in string");
		} else {
			int charEnd = i;
			addToken(new Token(token + "\"", STRING, charStart, charEnd));
			return i + 1;
		}
	}

	private int readTerminal(int i) {
		int charStart = i;
		i++;
		boolean seenToken = false;
		String  token     = "";
		while (i < text.length() && !seenToken) {
			char c = text.charAt(i++);
			if (c == '\'')
				seenToken = true;
			else if (c == '\\') {
				if (i < text.length()) {
					c = text.charAt(i++);
					token += c;
				} else
					throw new Error("EOF in token");
			} else
				token += c;
		}
		int charEnd = i;
		if (!seenToken)
			throw new Error("EOF in token starting at " + i);
		else
			addToken(new Token(token, TERM, charStart, charEnd));
		return i + 1;
	}

	private int readWhitespace(int i) {
		String  token        = "";
		int     charStart    = i;
		boolean isWhitespace = true;
		while (i < text.length() && isWhitespace) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c)) {
				token += c;
				i++;
			} else {
				isWhitespace = false;
				i--;
			}
		}
		int charEnd = i;
		addToken(new Token(token, WHITESPACE, charStart, charEnd));
		return i + 1;
	}

	@Override
	public void setCharPos(int pos) {
		int     i     = 0;
		boolean found = false;
		while (!found) {
			Token token = tokens.get(i++);
			if (pos >= token.getCharStart() && pos <= token.getCharEnd()) {
				tokenIndex = i;
				tokenCharIndex = pos - token.getCharStart();
				found = true;
			}
			if (!found && i == tokens.size()) {
				throw new Error("cannot set char pos.");
			}
		}
	}

	private boolean startsMultiLineComment(int i) {
		return text.startsWith("/*", i);
	}

	private boolean startsName(char c) {
		return Character.isAlphabetic(c);
	}

	private boolean startsNumber(char c) {
		return Character.isDigit(c);
	}

	private boolean startsSingleLineComment(int i) {
		return text.startsWith("//", i);
	}

	private boolean startsSpecial(char c) {
		return !Character.isAlphabetic(c) && !Character.isDigit(c) && !Character.isWhitespace(c);
	}

	private boolean startsString(char c) {
		return c == '\"';
	}

	private boolean startsTerminal(char c) {
		return c == '\'';
	}

	@Override
	public String token() {
		return getCurrentToken().getToken();
	}

	private void tokenize() {
		int i = 0;
		while (i < text.length()) {
			// System.out.println("Tokenize i = " + i + " text = " + text.substring(i, i + 10));
			char c = text.charAt(i);
			if (startsSingleLineComment(i)) {
				i = readSingleLineComment(i);
			} else if (startsMultiLineComment(i)) {
				i = readMultiLineComment(i);
			} else if (isSeq(i)) {
				i = readSeq(i);
			} else if (isSet(i)) {
				i = readSet(i);
			} else if (startsTerminal(c)) {
				i = readTerminal(i);
			} else if (startsNumber(c)) {
				i = readNumber(i);
			} else if (startsName(c)) {
				i = readName(i);
			} else if (startsString(c)) {
				i = readString(i);
			} else if (startsSpecial(c)) {
				i = readSpecial(i);
			} else if (isWhitespace(c)) {
				i = readWhitespace(i);
			} else
				i = readSpecial(i);
		}
		addToken(new Token("", EOF, text.length(), text.length()));
	}

	public String toString() {
		return "FileTokenizer(" + fileName + "," + tokens + ")";
	}

	@Override
	public int type() {
		return getCurrentToken().getType();
	}

}

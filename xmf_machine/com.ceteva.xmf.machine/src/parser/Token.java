package parser;

public class Token {

	private String token;
	private int    type;
	private int    charStart;
	private int    charEnd;

	public Token(String token, int type, int charStart, int charEnd) {
		this.token = token;
		this.type = type;
		this.charStart = charStart;
		this.charEnd = charEnd;
	}

	public String getToken() {
		return token;
	}

	public int getType() {
		return type;
	}

	public int getCharStart() {
		return charStart;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public String toString() {
		return "TOKEN(" + token + "," + type + "," + charStart + "," + charEnd + ")";
	}

}

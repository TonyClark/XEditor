package parser;

public interface Tokenizer {

	public String buffer();

	public int charCount();

	public int charPos();

	public String getString(int charStart, int charEnd);

	public int lastCharPos();

	public int lineCount();

	public void nextToken();

	public boolean peek(String next);

	public int readChar();

	public void setCharPos(int pos);

	public String token();

	public int type();

}

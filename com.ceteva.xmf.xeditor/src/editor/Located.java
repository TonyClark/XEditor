package editor;

public interface Located {

	public int getLineStart();

	public int getLineEnd();

	public void setLineStart(int linePos);

	public void setLineEnd(int linePos);

	public String getToolTipText();

	public String getFile();

	public void setFile(String file);

}

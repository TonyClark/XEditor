package editor;

public class LocatedAdapter implements Located {

  public static Located EMPTY = new LocatedAdapter("", -1, -1);

  private int           lineEnd;
  private int           lineStart;
  private String        file  = "NOT IMPLEmenTED";

  public LocatedAdapter(String file, int lineStart, int lineEnd) {
    this.file = file;
    this.lineStart = lineStart;
    this.lineEnd = lineEnd;
  }

  public LocatedAdapter() {
    super();
  }

  @Override
public int getLineEnd() {
    return lineEnd;
  }

  @Override
public int getLineStart() {
    return lineStart;
  }

  @Override
public void setLineEnd(int linePos) {
    lineEnd = linePos;
  }

  @Override
public void setLineStart(int linePos) {
    lineStart = linePos;
  }

  @Override
public String getToolTipText() {
    return null;
  }

  @Override
  public String getFile() {
    return file;
  }

  @Override
  public void setFile(String file) {
    this.file = file;
  }

}

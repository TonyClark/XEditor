package editor;

public class Unbound {

	private String name;
	private int    start, end;

	public Unbound(String name, int start, int end) {
		super();
		this.name = name;
		this.start = start;
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

}

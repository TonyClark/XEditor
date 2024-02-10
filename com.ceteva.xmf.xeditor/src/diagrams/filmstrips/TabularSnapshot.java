package diagrams.filmstrips;

public class TabularSnapshot {

	private Snapshot snapshot;
	private String label;

	public TabularSnapshot(String label, Snapshot snapshot) {
		super();
		this.label = label;
		this.snapshot = snapshot;
	}

	public Snapshot getSnapshot() {
		return snapshot;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String text) {
		label = text;
	}

}

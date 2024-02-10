package diagrams.filmstrips;

import java.util.Vector;

public class Step {

	private Snapshot     pre;
	private String       message;
	private String       doc;
	private String       value;
	private Vector<Step> steps;
	private Snapshot     post;

	public Step(Snapshot pre, String message, String doc, String value, Vector<Step> steps, Snapshot post) {
		super();
		this.pre = pre;
		this.message = message;
		this.doc = doc;
		this.value = value;
		this.steps = steps;
		this.post = post;
	}

	public void populate(StepNode parent) {
		for (Step s : steps) {
			StepNode n = new StepNode(s);
			parent.add(n);
			s.populate(n);
		}
	}

	public String getDoc() {
		return doc;
	}

	public Snapshot getPre() {
		return pre;
	}

	public String getMessage() {
		return message;
	}

	public String getValue() {
		return value;
	}

	public Vector<Step> getSteps() {
		return steps;
	}

	public Snapshot getPost() {
		return post;
	}

	public String getLabel() {
		return message + " => " +  value;
	}

}

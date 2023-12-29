package diagrams.filmstrips;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

public class Filmstrip {

	private Step                   step;
	private Vector<Vector<String>> checks;

	public Filmstrip(Step step, Vector<Vector<String>> checks) {
		super();
		this.step   = step;
		this.checks = checks;
	}

	public void populate(DefaultMutableTreeNode root) {
		StepNode n = new StepNode(step);
		root.add(n);
		step.populate(n);
	}

	public Step getStep() {
		return step;
	}

	public Vector<Vector<String>> getChecks() {
		return checks;
	}

}

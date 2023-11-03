package diagrams.filmstrips;

import javax.swing.tree.DefaultMutableTreeNode;

public class Filmstrip {

	private Step step;

	public Filmstrip(Step step) {
		super();
		this.step = step;
	}

	public void populate(DefaultMutableTreeNode root) {
		StepNode n = new StepNode(step);
		root.add(n);
		step.populate(n);
	}

}

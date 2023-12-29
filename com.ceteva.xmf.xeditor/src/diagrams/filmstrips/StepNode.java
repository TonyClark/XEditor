package diagrams.filmstrips;

import javax.swing.tree.DefaultMutableTreeNode;

public class StepNode extends DefaultMutableTreeNode {

	private Step step;

	public StepNode(Step step) {
		super(step.getMessage());
		this.step = step;
	}

	public Step getStep() {
		return step;
	}

}

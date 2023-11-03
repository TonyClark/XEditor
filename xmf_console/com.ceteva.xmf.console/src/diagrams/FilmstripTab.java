package diagrams;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import console.Console;
import diagrams.filmstrips.Filmstrip;
import diagrams.filmstrips.FilmstripNode;
import diagrams.filmstrips.Step;
import diagrams.filmstrips.StepNode;

public class FilmstripTab extends DiagramTab {

	private SnapshotPanel pre, post = null;
	private Console       console     = null;
	private DiagramFrame  frame       = null;
	private String        name;
	private Filmstrip     filmstrip;
	private LabelledText  message     = new LabelledText("Message");
	private LabelledText  result      = new LabelledText("Result");
	private JSplitPane    step        = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private StepTree      stepTree;
	private JPanel        currentStep = new JPanel();
	private JSplitPane    split       = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private FilmstripNode root;

	public FilmstripTab(Console console, DiagramFrame frame, String name, Filmstrip filmstrip) {
		this.console = console;
		this.frame = frame;
		this.filmstrip = filmstrip;
		this.name = name;
		this.pre = new SnapshotPanel(console);
		this.post = new SnapshotPanel(console);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		populateStepTree();
		split.setLeftComponent(new JScrollPane(stepTree));
		split.setRightComponent(currentStep);
		currentStep.setLayout(new BorderLayout());
		step.setTopComponent(new JScrollPane(pre));
		step.setBottomComponent(new JScrollPane(post));
		currentStep.add(step, BorderLayout.CENTER);
		currentStep.add(message, BorderLayout.NORTH);
		currentStep.add(result, BorderLayout.SOUTH);
	}

	private void populateStepTree() {
		root = new FilmstripNode(name);
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		stepTree = new StepTree(console, treeModel, frame);
		filmstrip.populate(root);
		stepTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) stepTree.getLastSelectedPathComponent();

				if (node == null)
					return;

				if (node instanceof StepNode) {
					StepNode stepNode = (StepNode) node;
					Step     step     = stepNode.getStep();
					pre.setSVG(step.getPre().getSVG());
					post.setSVG(step.getPost().getSVG());
					message.setText(step.getMessage());
					result.setText(step.getValue());
				}
			}
		});
	}

}

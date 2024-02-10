package filtertree;

import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import console.browser.ElementNode;
import console.browser.RootNode;

public class TreeFilterDecorator {

	private final JTree                                              tree;
	private DefaultMutableTreeNode                                   originalRootNode;
	private BiPredicate<Object, String>                              userObjectMatcher;
	private Function<DefaultMutableTreeNode, DefaultMutableTreeNode> copyNode;
	private JTextField                                               filterField;

	public TreeFilterDecorator(JTree tree, BiPredicate<Object, String> userObjectMatcher, Function<DefaultMutableTreeNode, DefaultMutableTreeNode> copyNode) {
		this.tree = tree;
		this.originalRootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		this.copyNode = copyNode;
		this.userObjectMatcher = userObjectMatcher;
	}

	public static TreeFilterDecorator decorate(JTree tree, BiPredicate<Object, String> userObjectMatcher, Function<DefaultMutableTreeNode, DefaultMutableTreeNode> copyNode) {
		TreeFilterDecorator tfd = new TreeFilterDecorator(tree, userObjectMatcher, copyNode);
		tfd.init();
		return tfd;
	}

	public JTextField getFilterField() {
		return filterField;
	}

	private void init() {
		initFilterField();
	}

	private void initFilterField() {
		filterField = new JTextField(15);
		filterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTree();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTree();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterTree();
			}
		});
	}

	private void filterTree() {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		String           text  = filterField.getText().trim().toLowerCase();
		if (text.equals("") && tree.getModel().getRoot() != originalRootNode) {
			model.setRoot(originalRootNode);
			JTreeUtil.setTreeExpandedState(tree, false);
		} else {
			DefaultMutableTreeNode newRootNode = matchAndBuildNode(text, originalRootNode);
			model.setRoot(newRootNode);
			JTreeUtil.setTreeExpandedState(tree, true);
		}
	}

	private DefaultMutableTreeNode matchAndBuildNode(final String text, DefaultMutableTreeNode oldNode) {
		if (oldNode.isLeaf())
			if (userObjectMatcher.test(oldNode.toString(), text)) {
				return copyNode.apply(oldNode);
			} else
				return null;
		else {
			DefaultMutableTreeNode newNode = copyNode.apply(oldNode);
			for (DefaultMutableTreeNode childOldNode : JTreeUtil.children(oldNode)) {
				DefaultMutableTreeNode newMatchedChildNode = matchAndBuildNode(text, childOldNode);
				if (newMatchedChildNode != null)
					newNode.add(newMatchedChildNode);

			}
			if (newNode.getChildCount() > 0) {
				return newNode;
			} else if (oldNode != originalRootNode && !userObjectMatcher.test(oldNode.toString(), text))
				return null;
			else
				return newNode;
		}
	}

	public void setRoot(ElementNode elementNode) {
		originalRootNode = elementNode;
	}
}
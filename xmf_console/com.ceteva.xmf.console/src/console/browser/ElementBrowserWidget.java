package console.browser;

import java.awt.BorderLayout;
import java.awt.Label;
import java.util.Vector;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import console.Console;
import filtertree.TreeFilterDecorator;

public class ElementBrowserWidget extends JPanel {

	private ElementBrowser      browser;
	private TreeFilterDecorator filterDecorator;

	public ElementBrowserWidget(Console console) {
		browser = new ElementBrowser(console);
		setLayout(new BorderLayout());
		filterDecorator = TreeFilterDecorator.decorate(browser, createUserObjectMatcher(), createCopyNode());
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BorderLayout());
		filterPanel.add(new Label("filter:"), BorderLayout.WEST);
		filterPanel.add(filterDecorator.getFilterField(), BorderLayout.CENTER);
		add(filterPanel, BorderLayout.NORTH);
		add(new JScrollPane(browser), BorderLayout.CENTER);
	}

	private static BiPredicate<Object, String> createUserObjectMatcher() {
		return (userObject, textToFilter) -> {
			return userObject.toString().toLowerCase().contains(textToFilter);
		};
	}

	private Function<DefaultMutableTreeNode, DefaultMutableTreeNode> createCopyNode() {
		return (n) -> (DefaultMutableTreeNode) n.clone();
	}

	public ElementBrowser getElementBrowser() {
		return browser;
	}

	public void setRoot(Vector<Object> tree) {
		filterDecorator.setRoot(browser.setRoot(tree));
	}

	public void setElementImage(int id, String imageFile) {
		browser.setElementImage(id, imageFile);
	}

	public void refresh(int id) {
		browser.refresh(id);
	}

	public void setHoverText(int id, String text) {
		browser.setHoverText(id,text);
	}
}

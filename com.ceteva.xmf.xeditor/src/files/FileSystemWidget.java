package files;

import java.awt.BorderLayout;
import java.awt.Label;
import java.io.File;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import console.Console;
import filtertree.TreeFilterDecorator;

public class FileSystemWidget extends JPanel {

	private FileSystem fileSystem;

	public FileSystemWidget(Console console, String roots) {
		this.fileSystem = new FileSystem(console, roots);
		setLayout(new BorderLayout());
		TreeFilterDecorator filterDecorator = TreeFilterDecorator.decorate(fileSystem, createUserObjectMatcher(), createCopyNode());
		JPanel              filterPanel     = new JPanel();
		filterPanel.setLayout(new BorderLayout());
		filterPanel.add(new Label("filter:"), BorderLayout.WEST);
		filterPanel.add(filterDecorator.getFilterField(), BorderLayout.CENTER);
		add(filterPanel, BorderLayout.NORTH);
		add(new JScrollPane(fileSystem), BorderLayout.CENTER);
	}

	private Function<DefaultMutableTreeNode, DefaultMutableTreeNode> createCopyNode() {
		return (n) -> (DefaultMutableTreeNode) n.clone();
	}

	private static BiPredicate<Object, String> createUserObjectMatcher() {
		return (userObject, textToFilter) -> {
			return userObject.toString().toLowerCase().contains(textToFilter);
		};
	}

	public void select(File file) {
		fileSystem.select(file);
	}

	public void clear(File file) {
		fileSystem.clear(file);
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public void browseDir(File dir) {
		fileSystem.browseDir(dir);
	}

}

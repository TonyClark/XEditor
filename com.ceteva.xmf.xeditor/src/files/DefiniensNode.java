package files;

import javax.swing.JTree;

import console.Console;
import editor.Definiens;

public class DefiniensNode extends FileSystemNode {

	public DefiniensNode(Definiens data, String icon, Console console, JTree tree) {
		super(data, console, tree, icon);
	}

	public Definiens getDefiniens() {
		return (Definiens) getUserObject();
	}

}

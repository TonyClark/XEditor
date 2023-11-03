package console;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

public class BackLabel extends JLabel implements MouseListener {

	private Console console;

	public BackLabel(Console console) {
		super("«");
		this.console = console;
		addMouseListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown() || e.isMetaDown())
			selectFromList();
		else
			console.getXmfPanel().back();
	}

	private void selectFromList() {
		Vector<File> files = console.getXmfPanel().getPreviousFiles();
		if (files.size() > 0) {
			File[] choices = new File[files.size()];
			files.copyInto(choices);
			File choice = (File) JOptionPane.showInputDialog(null, "Choose now...", "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
			    // default
			    // icon
			    choices, // Array of choices
			    choices[0]); // Initial choice
			if (choice != null)
				console.load(choice);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getToolTipText() {
		return Console.htmlList(console.getXmfPanel().getPreviousFiles());
	}

}

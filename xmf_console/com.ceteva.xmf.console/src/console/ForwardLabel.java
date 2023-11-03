package console;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

public class ForwardLabel extends JLabel implements MouseListener {

	private Console console;

	public ForwardLabel(Console console) {
		super("»");
		this.console = console;
		addMouseListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown() || e.isMetaDown())
			selectFromList();
		else
			console.getXmfPanel().forward();
	}

	private void selectFromList() {
		Vector<File> files = console.getXmfPanel().getNextFiles();
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public String getToolTipText() {
		return Console.htmlList(console.getXmfPanel().getNextFiles());
	}
}

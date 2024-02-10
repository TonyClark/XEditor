package console.browser;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import console.Console;

public class MLeaf extends MItem {

	private String label;
	private int    id;

	public MLeaf(String label, int id) {
		super();
		this.label = label;
		this.id = id;
	}

	@Override
	public void populate(JMenu menu, int elementId) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
			Console.send("browserPopupMenuSelected", new Object[] { id, elementId });
		}));
		menu.add(item);
	}

	@Override
	public void populate(JPopupMenu popup, int elementId) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
			Console.send("browserPopupMenuSelected", new Object[] { id, elementId });
		}));
		popup.add(item);
	}

}

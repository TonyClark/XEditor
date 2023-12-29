package console.browser;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class Menu extends MItem {

	private String label;
	private Vector<MItem> menuItems = new Vector<MItem>();

	@Override
	public void populate(JMenu menu,int elementId) {
		JMenu m = new JMenu(label);
		menu.add(m);
		for (MItem item : menuItems) {
			item.populate(m,elementId);
		}
	}

	public void add(MItem item) {
		menuItems.add(item);
	}

	@Override
	public void populate(JPopupMenu popup, int elementId) {
		JMenu m = new JMenu(label);
		popup.add(m);
		for (MItem item : menuItems) {
			item.populate(m,elementId);
		}
	}

}

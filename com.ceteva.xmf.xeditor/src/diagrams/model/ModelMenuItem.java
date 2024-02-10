package diagrams.model;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ModelMenuItem {

	private String name;

	public ModelMenuItem(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addTo(JMenu menu, Model model, ModelDiagramPanel panel) {
		JMenuItem item = new JMenuItem(name);
		menu.add(item);
		item.addActionListener((e) -> model.menuEvent(name, panel));
	}

	public void addTo(JPopupMenu menu, Model model, ModelDiagramPanel panel) {
		JMenuItem item = new JMenuItem(name);
		menu.add(item);
		item.addActionListener((e) -> model.menuEvent(name, panel));
	}

}

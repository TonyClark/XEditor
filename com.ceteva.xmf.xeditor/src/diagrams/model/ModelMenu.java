package diagrams.model;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class ModelMenu extends ModelMenuItem {

	private Vector<ModelMenuItem> items;

	public ModelMenu(String name, Vector<ModelMenuItem> items) {
		super(name);
		this.items = items;
	}

	public void addTo(JMenu menu, Model model, ModelDiagramPanel panel) {
		JMenu m = new JMenu(getName());
		menu.add(m);
		for (ModelMenuItem i : items) {
			i.addTo(m, model,panel);
		}

	}

	public void add(ModelMenuItem item) {
		items.add(item);
	}

}

package console.browser;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public abstract class MItem {

	public abstract void populate(JPopupMenu popup,int elementId);

	public abstract void populate(JMenu menu,int elementId);

}

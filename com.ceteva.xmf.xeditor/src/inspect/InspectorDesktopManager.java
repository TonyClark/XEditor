package inspect;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class InspectorDesktopManager extends DefaultDesktopManager {

	InspectorDesktop desktop;

	public InspectorDesktopManager(InspectorDesktop desktop) {
		super();
		this.desktop = desktop;
	}

	@Override
	public void dragFrame(JComponent f, int x, int y) {
		super.dragFrame(f, x, y);
		desktop.repaint();
	}

	@Override
	public void closeFrame(JInternalFrame f) {
		super.closeFrame(f);
		desktop.closeInspector((Inspector) f);
	}

	@Override
	public void iconifyFrame(JInternalFrame f) {
		super.iconifyFrame(f);
		desktop.iconifyInspector((Inspector) f);
	}

	@Override
	public void deiconifyFrame(JInternalFrame f) {
		super.deiconifyFrame(f);
		desktop.deiconifyInspector((Inspector) f);
	}

}

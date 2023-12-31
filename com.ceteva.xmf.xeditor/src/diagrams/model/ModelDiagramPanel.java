package diagrams.model;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import console.Console;
import diagrams.DiagramFrame;
import diagrams.DiagramPanel;
import web.SVG;

public class ModelDiagramPanel extends DiagramPanel implements ComponentListener {

	private Model               model;
	private VisibilitySelection visibility;
	private DiagramFrame        frame;

	public ModelDiagramPanel(Console console, Model model, DiagramFrame frame) {
		super(console);
		this.model = model;
		this.frame = frame;
		visibility = new VisibilitySelection(console, this);
		regenerate();
		frame.addComponentListener(this);
	}

	public JPopupMenu popupMenu(MouseEvent e) {
		JPopupMenu menu = super.popupMenu(e);
		JMenuItem visibility = new JMenuItem("Visibility");
		menu.add(visibility);
		visibility.addActionListener((x) -> setVisibility());
		JMenu lineStyle = new JMenu("Lines");
		for (EdgeStyle style : EdgeStyle.values()) {
			JMenuItem item = new JMenuItem(style.toString());
			lineStyle.add(item);
			item.addActionListener((x) -> setEdgeStyle(style));
		}
		menu.add(lineStyle);
		return menu;
	}

	private void setEdgeStyle(EdgeStyle style) {
		model.setEdgeStyle(style);
		regenerate();
	}

	private void setVisibility() {
		SwingUtilities.invokeLater(() -> {
			try {
				visibility.setSize(400, frame.getHeight());
				visibility.setLocation(frame.getX() + frame.getWidth() + 10, frame.getY());
				visibility.setVisible(true);
			} catch (Exception error) {
				error.printStackTrace();
			}
		});
	}

	public Model getModel() {
		return model;
	}

	public VisibilitySelection getVisibility() {
		return visibility;
	}

	public void regenerate() {
		setSVG(SVG.plantUML2SVG(model.getPlantUML()));
	}

	public void showImports(String path, boolean show) {
		model.showImports(path, show);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		resetVisibility();
	}

	private void resetVisibility() {
		visibility.setLocation(frame.getX() + frame.getWidth() + 10, frame.getY());
		visibility.setSize(visibility.getWidth(), frame.getHeight());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		resetVisibility();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		visibility.setVisible(false);
	}

}

package inspect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import editor.ClipboardImage;

public class InspectorDesktop extends JDesktopPane implements MouseListener, MouseMotionListener {

	public Vector<Inspector> inspectors = new Vector<Inspector>();
	private Vector<Edge> edges = new Vector<Edge>();
	private Waypoint selectedWaypoint;
	private RubberBand rubberBand = new RubberBand();

	public InspectorDesktop() {
		setBackground(Color.white);
		setTransferHandler(new DesktopTransferHandler());
		setAutoscrolls(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setDesktopManager(new InspectorDesktopManager(this));
		try {
			// Closing JInternalFrames seems to fail with a desktop manager without setting
			// the look and feel.
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		if (getAllFrames().length > 0) {
			Rectangle max = getAllFrames()[0].getNormalBounds();
			for (JInternalFrame jif : this.getAllFrames()) {
				max.add(jif.getNormalBounds());
				Rectangle outline = (Rectangle) jif.getClientProperty("outlineBounds");
				if (outline != null) {
					max.add(outline);
				}
			}
			int x1 = max.width + (max.x * 2) < prefSize.width ? prefSize.width : max.width + (max.x * 2);
			int y1 = max.height + (max.y * 2) < prefSize.height ? prefSize.height : max.height + (max.y * 2);
			return new Dimension(x1, y1);
		}
		return new Dimension(0, 0);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger())
			popup(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selectedWaypoint = null;
		if (e.isPopupTrigger())
			popup(e);
		else {
			rubberBand.reset(e.getX(), e.getY());
			Point p = new Point(e.getX(), e.getY());
			Edge edge = closestEdge(p);
			if (edge != null) {
				Waypoint w = edge.getWaypoint(p);
				if (w != null) {
					selectedWaypoint = w;
					repaint();
				} else {
					selectedWaypoint = edge.addWaypoint(p);
					repaint();
				}
			}
		}
	}

	private Edge closestEdge(Point point) {
		for (Edge edge : edges) {
			if (Math.abs(edge.distanceTo(point)) < 10)
				return edge;
		}
		return null;
	}

	private void popup(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem gather = new JMenuItem("Gather");
		gather.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		gather.addActionListener((x) -> {
			gather();
		});
		menu.add(gather);
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener((x) -> copyToClipboard());
		menu.add(copy);
		menu.show(this, e.getX(), e.getY());
	}

	private void copyToClipboard() {
		boolean rubberBandActive = rubberBand.isActive();
		int x = rubberBandActive ? rubberBand.x : 0;
		int y = rubberBandActive ? rubberBand.y : 0;
		int width = rubberBandActive ? rubberBand.width: getWidth();
		int height = rubberBandActive ? rubberBand.height : getHeight();
		BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.translate(-x,-y);
		g.setColor(Color.white);
		g.fillRect(0, 0, width,height);
		paint(g);
		new ClipboardImage(image);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		checkWaypoints();
		selectedWaypoint = null;
		repaint();
	}

	private void checkWaypoints() {
		// Remove any waypoints that are on straight line segments...
		for (Edge edge : edges) {
			edge.checkWaypoints();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		refreshLines();
		paintEdges(g2);
		rubberBand.paint(g2);
	}

	private void paintEdges(Graphics2D g2) {
		for (Edge edge : edges) {
			edge.paint(g2);
		}
	}

	private void refreshLines() {
		for (Inspector i1 : getInspectors()) {
			for (Inspector i2 : getInspectors()) {
				for (Edge e : i1.edges(i2)) {
					if (!hasEdge(e.getSource(), e.getTarget(), e.getName()))
						edges.add(e);
				}
			}
		}
	}

	private boolean hasEdge(Inspector source, Inspector target, String name) {
		for (Edge edge : edges) {
			if (edge.getName().equals(name) && edge.getSource() == source && edge.getTarget() == target) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selectedWaypoint != null) {
			selectedWaypoint.move(e.getX(), e.getY());
			repaint();
		} else {
			rubberBand.update(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	public Vector<Edge> edgesIncidentOn(Inspector inspector) {
		Vector<Edge> incident = new Vector<Edge>();
		for (Edge e : edges) {
			if (e.getSource() == inspector || e.getTarget() == inspector) {
				incident.add(e);
			}
		}
		return incident;
	}

	public void removeEdge(Edge e) {
		edges.remove(e);
		repaint();
	}

	public void addInspector(Inspector inspector) {
		inspectors.add(inspector);
		add(inspector);
		repaint();
	}

	public void removeInspector(Inspector inspector) {
		inspectors.remove(inspector);
		repaint();
	}

	public Inspector getInspector(int objectHandle) {
		for (Inspector i : inspectors) {
			if (i.getObjectHandle() == objectHandle) {
				return i;
			}
		}
		return null;
	}

	public void gather() {
		SwingUtilities.invokeLater(() -> {
			int x = 50;
			int y = 50;
			for (Inspector i : inspectors) {
				i.setLocation(x, y);
				try {
					i.setSelected(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				x += 50;
				y += 50;
			}
		});
	}

	public Vector<Inspector> getInspectors() {
		return inspectors;
	}

	public void closeInspector(Inspector inspector) {
		for (Edge e : edgesIncidentOn(inspector)) {
			removeEdge(e);
		}
		inspectors.remove(inspector);
		repaint();
	}

	public void iconifyInspector(Inspector inspector) {
		for (Edge e : edgesIncidentOn(inspector)) {
			e.setHidden(true);
		}
		inspectors.remove(inspector);
		repaint();
	}

	public void deiconifyInspector(Inspector inspector) {
		inspectors.add(inspector);
		for (Edge e : edgesIncidentOn(inspector)) {
			if (inspectors.contains(e.getSource()) && inspectors.contains(e.getTarget()))
				e.setHidden(false);
		}
		repaint();
	}

	public Inspector getSnapshot(int objectHandle) {
		for (Inspector inspector : inspectors) {
			if (inspector.getObjectHandle() == objectHandle) {
				return inspector;
			}
		}
		return null;
	}

}

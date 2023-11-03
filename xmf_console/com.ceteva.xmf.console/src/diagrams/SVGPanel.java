package diagrams;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.Interactor;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.LinkActivationEvent;
import org.apache.batik.swing.svg.SVGUserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import console.Console;

public abstract class SVGPanel extends JSVGCanvas implements java.awt.event.MouseWheelListener, java.awt.event.MouseMotionListener, java.awt.event.MouseListener, KeyListener {

	/**
	 * A swing action to Rotate the canvas.
	 */
	public class RotateAction extends AffineAction {
		public RotateAction(double theta) {
			super(AffineTransform.getRotateInstance(theta));
		}
	}

	/**
	 * A swing action to Pan/scroll the canvas.
	 */
	public class ScrollAction extends AffineAction {
		public ScrollAction(double tx, double ty) {
			super(AffineTransform.getTranslateInstance(tx, ty));
		}
	}

	/**
	 * A swing action to scroll the canvas down, by a fixed amount
	 */
	public class ScrollDownAction extends ScrollAction {
		public ScrollDownAction(int inc) {
			super(0, -inc);

		}
	}

	/**
	 * A swing action to scroll the canvas to the left, by a fixed amount
	 */
	public class ScrollLeftAction extends ScrollAction {
		public ScrollLeftAction(int inc) {
			super(inc, 0);
		}
	}

	/**
	 * A swing action to scroll the canvas to the right, by a fixed amount
	 */
	public class ScrollRightAction extends ScrollAction {
		public ScrollRightAction(int inc) {
			super(-inc, 0);
		}
	}

	/**
	 * A swing action to scroll the canvas up, by a fixed amount
	 */
	public class ScrollUpAction extends ScrollAction {
		public ScrollUpAction(int inc) {
			super(0, inc);
		}
	}

	public class ZoomAction extends AffineAction {

		public ZoomAction(double scale) {
			super(AffineTransform.getScaleInstance(scale, scale));
		}

		public ZoomAction(double scaleX, double scaleY) {
			super(AffineTransform.getScaleInstance(scaleX, scaleY));
		}
	}

	/**
	 * A swing action to zoom in the canvas.
	 */
	public class ZoomInAction extends ZoomAction {
		ZoomInAction() {
			super(2);
		}
	}

	/**
	 * A swing action to zoom out the canvas.
	 */
	public class ZoomOutAction extends ZoomAction {
		ZoomOutAction() {
			super(.5);
		}
	}

	private static int                 wheelCount     = 0;

	private static final int           ZOOM_PHASE     = 10;

	private int mouseX, mouseY;
	private double                     zoom           = 1.0;
	private int                        regionX        = -1;
	private int                        regionY        = -1;
	private Console                    console;
	private Vector<Consumer<Graphics>> paintListeners = new Vector<Consumer<Graphics>>();
	public SVGPanel(Console console) {
		super(new SVGUserAgentAdapter(), true, true);
		this.console = console;
		setDocumentState(JSVGComponent.ALWAYS_DYNAMIC);
		addMouseWheelListener(this);
		List<Interactor> intl = getInteractors();
		intl.add(zoomInteractor);
		installActions();
		installKeyboardActions();
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		addLinkActivationListener((l) -> linkClicked(l));
	}

	public void add(Consumer<Graphics> listener) {
		paintListeners.add(listener);
	}

	protected void copy() {
		int           x      = regionX == -1 ? 0 : mouseX;
		int           y      = regionY == -1 ? 0 : mouseY;
		int           width  = regionX == -1 ? getWidth() : (regionX - mouseX);
		int           height = regionY == -1 ? getHeight() : (regionY - mouseY);
		BufferedImage image  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D    g      = (Graphics2D) image.getGraphics();
		g.translate(-x, -y);
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		regionX = -1;
		regionY = -1;
		g.setColor(Color.black);
		paint(g);
		new ClipboardImage(image);
	}

	@Override
	protected UserAgent createUserAgent() {
		CanvasUserAgent agent = new CanvasUserAgent() {

			@Override
			public void displayError(Exception arg0) {
				// TODO Auto-generated method stub
				// super.displayError(arg0);
			}

			@Override
			public void displayError(String arg0) {
				// TODO Auto-generated method stub
				// super.displayError(arg0);
			}

			@Override
			public float getPixelToMM() {
				// TODO Auto-generated method stub
				return (float) 1.0;
			}

			@Override
			public float getPixelUnitToMillimeter() {
				// TODO Auto-generated method stub
				return (float) 1.0;
			}
		};
		return agent;
	}

	private void dragDisplay(MouseEvent e) {
		dragX(e.getX());
		dragY(e.getY());
		mouseX = e.getX();
		mouseY = e.getY();
	}

	private void dragSelectionRegion(MouseEvent e) {
		regionX = e.getX();
		regionY = e.getY();
		repaint();
	}

	private void dragX(int x) {
		if (x < mouseX)
			new ScrollRightAction(mouseX - x).actionPerformed(new ActionEvent(this, 0, null));
		else
			new ScrollLeftAction(x - mouseX).actionPerformed(new ActionEvent(this, 0, null));
	}

	private void dragY(int y) {
		if (y < mouseY)
			new ScrollDownAction(mouseY - y).actionPerformed(new ActionEvent(this, 0, null));
		else
			new ScrollUpAction(y - mouseY).actionPerformed(new ActionEvent(this, 0, null));
	}

	public void exportImage(String imageName) {
		BufferedImage image    = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D    graphics = image.createGraphics();
		paint(graphics);
		graphics.dispose();
		try {
			System.out.println("Exporting image: " + imageName);
			FileOutputStream out = new FileOutputStream(imageName);
			ImageIO.write(image, "png", out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void installActions() {
		javax.swing.ActionMap actionMap = getActionMap();

		actionMap.put(SCROLL_RIGHT_ACTION, new ScrollRightAction(10));
		actionMap.put(SCROLL_LEFT_ACTION, new ScrollLeftAction(10));
		actionMap.put(SCROLL_UP_ACTION, new ScrollUpAction(10));
		actionMap.put(SCROLL_DOWN_ACTION, new ScrollDownAction(10));

		actionMap.put(FAST_SCROLL_RIGHT_ACTION, new ScrollRightAction(30));
		actionMap.put(FAST_SCROLL_LEFT_ACTION, new ScrollLeftAction(30));
		actionMap.put(FAST_SCROLL_UP_ACTION, new ScrollUpAction(30));
		actionMap.put(FAST_SCROLL_DOWN_ACTION, new ScrollDownAction(30));

		actionMap.put(ZOOM_IN_ACTION, new ZoomInAction());
		actionMap.put(ZOOM_OUT_ACTION, new ZoomOutAction());

		actionMap.put(RESET_TRANSFORM_ACTION, new ResetTransformAction());
	}

	protected void installKeyboardActions() {

		InputMap  inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		KeyStroke key;

		key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		inputMap.put(key, SCROLL_RIGHT_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		inputMap.put(key, SCROLL_LEFT_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		inputMap.put(key, SCROLL_UP_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		inputMap.put(key, SCROLL_DOWN_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK);
		inputMap.put(key, FAST_SCROLL_RIGHT_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK);
		inputMap.put(key, FAST_SCROLL_LEFT_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK);
		inputMap.put(key, FAST_SCROLL_UP_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK);
		inputMap.put(key, FAST_SCROLL_DOWN_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK);
		inputMap.put(key, ZOOM_IN_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK);
		inputMap.put(key, ZOOM_OUT_ACTION);

		key = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK);
		inputMap.put(key, RESET_TRANSFORM_ACTION);
	}

	public void keyPressed(KeyEvent e) {
		if (e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_C)
			copy();
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public abstract void linkClicked(LinkActivationEvent l);

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
			JPopupMenu menu = popupMenu(e);
			if (menu != null) {
				menu.show(this, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.isMetaDown())
			dragSelectionRegion(e);
		else
			dragDisplay(e);
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
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isPopupTrigger()) {
			mouseX = e.getX();
			mouseY = e.getY();
			regionX = -1;
			regionY = -1;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int r = e.getWheelRotation();
		if (r != 0) {
			wheelCount++;
			if (wheelCount % ZOOM_PHASE == 0) {
				if (e.getWheelRotation() < 0) {
					zoom *= 1.1;
					new ZoomAction(1.1).actionPerformed(new ActionEvent(e.getSource(), 0, null));
				} else {
					zoom *= 0.9;
					new ZoomAction(0.9).actionPerformed(new ActionEvent(e.getSource(), 0, null));
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (regionX != -1) {
			g.setColor(Color.black);
			g.drawRect(mouseX, mouseY, regionX - mouseX, regionY - mouseY);
		}
		for(Consumer<Graphics> listener : paintListeners) {
			listener.accept(g);
		}
	}

	public JPopupMenu popupMenu(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem  save = new JMenuItem("Save");
		save.addActionListener((x) -> save());
		menu.add(save);
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener((x) -> copy());
		menu.add(copy);
		return menu;
	}

	private void save() {
		int returnVal = console.getFileChooser().showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = console.getFileChooser().getSelectedFile();
			exportImage(file.getAbsolutePath());
		}
	}

	public void setSVG(String svg) {
		String                parser  = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		SVGDocument           document;
		setEnableZoomInteractor(true);
		try {
			document = factory.createSVGDocument("", new ByteArrayInputStream(svg.getBytes("UTF-8")));
			setSVGDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

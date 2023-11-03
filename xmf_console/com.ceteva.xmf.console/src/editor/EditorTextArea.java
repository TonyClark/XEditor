package editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import console.Console;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import repl.XMFREPL;

public class EditorTextArea extends TextEditorPane implements KeyListener, MouseMotionListener, MouseListener, SearchListener {

	private class GoToLineAction extends AbstractAction {

		public GoToLineAction() {
			super("Go To Line...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog((Dialog) getParent());
			dialog.setMaxLineNumberAllowed(EditorTextArea.this.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line > 0) {
				try {
					EditorTextArea.this.setCaretPosition(EditorTextArea.this.getLineStartOffset(line - 1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(EditorTextArea.this);
					ble.printStackTrace();
				}
			}
		}

	}

	private class ShowFindDialogAction extends AbstractAction {

		public ShowFindDialogAction() {
			super("Find...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}

	private class ShowReplaceDialogAction extends AbstractAction {

		public ShowReplaceDialogAction() {
			super("Replace...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}

	private static final float                FONT_INCREMENT_SIZE = 1;
	private static final int                  FONT_MAX_SIZE       = 36;
	private static final int                  FONT_MIN_SIZE       = 6;
	private static final int                  DEFAULT_FONT_SIZE   = 16;
	private static final double               PINCH_SCALE         = 0.001;
	private static final int                  PARSE_DELAY         = 1000;
	private static final Color                UNBOUND             = new Color(255, 0, 0, 127);
	public static Font                        EDITOR_FONT         = new Font("Monaco", Font.PLAIN, DEFAULT_FONT_SIZE);
	public static int                         alpha               = 100;
	public static Color                       definiensColour     = new Color(255, 0, 0, alpha);
	public static Color                       definiendumColour   = new Color(0, 0, 255, alpha);
	private static Stroke                     continuous          = new BasicStroke(2);
	private static Stroke                     dashed              = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5, 5 }, 0);
	private static Stroke                     dotted              = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1, 2 }, 0);
	private static Hashtable<String, Integer> caretPositions      = new Hashtable<String, Integer>();

	public static void drawArrow(Graphics g, int tipx, int tipy, int tailx, int taily, boolean filled, double angle, double head, Color fill, Color line) {
		double     phi   = Math.toRadians(angle);
		double     dy    = tipy - taily;
		double     dx    = tipx - tailx;
		double     theta = Math.atan2(dy, dx);
		double     x, y, rho = theta + phi;
		Graphics2D g2d   = (Graphics2D) g;

		Color      oldFG = g.getColor();
		Stroke     s     = g2d.getStroke();
		g2d.setStroke(continuous);

		g.setColor(line);
		if (!filled) {
			for (int j = 0; j < 2; j++) {
				x = tipx - head * Math.cos(rho);
				y = tipy - head * Math.sin(rho);
				g.drawLine(tipx, tipy, (int) x, (int) y);
				rho = theta - phi;
			}
		} else {
			int   width  = 2;
			int[] points = new int[6];
			points[0] = tipx;
			points[1] = tipy;
			for (int j = 0; j < 2; j++) {
				points[(j * 2) + 2] = (int) (tipx - head * Math.cos(rho));
				points[(j * 2) + 3] = (int) (tipy - head * Math.sin(rho));
				g.drawLine(tipx, tipy, points[(j * 2) + 2], points[(j * 2) + 3]);
				rho = theta - phi;
			}
			g.drawLine(points[2], points[3], points[4], points[5]);
			g.setColor(fill);
			g.fillPolygon(new Polygon(new int[] { points[0], points[2], points[4] }, new int[] { points[1], points[3], points[5] }, 3));
		}
		g.setColor(oldFG);
		g2d.setStroke(s);
	}

	public static int getLine(int index, String text) {
		int line = 0;
		for (int i = 0; i < index && i < text.length(); i++) {
			if (text.charAt(i) == '\n')
				line++;
		}
		return line;
	}

	public static String read(File file) {
		String path = file.getAbsolutePath();
		try {
			FileInputStream in  = new FileInputStream(path);
			StringBuffer    buf = new StringBuffer();
			int             c   = in.read();
			while (c != -1) {
				buf.append((char) c);
				c = in.read();
			}
			in.close();
			String text = buf.toString();
			// Just in case we have Windows text files....
			return text.replace("\r", "");
		} catch (FileNotFoundException e) {
			return e.toString();
		} catch (IOException e) {
			return e.toString();
		}
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image         tmp  = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D    g2d  = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static void setFont(String fontName, int fontSize) {
		EDITOR_FONT = new Font(fontName, Font.PLAIN, fontSize);
	}

	private Color                     background            = Color.white;
	private Vector<String>            languageMenuItemNames = new Vector<String>();
	private String                    tooltipText           = null;
	private Timer                     timer                 = new Timer(true);
	private TimerTask                 task                  = null;
	private Font                      font                  = EDITOR_FONT;
	private double                    lastPinch             = 0.0;
	private Console                   console;
	private File                      file                  = null;
	private Set<Point>                locations             = new HashSet<Point>();
	private Set<Unbound>              unbound               = new HashSet<Unbound>();
	private Set<Definiendum>          definiendum           = new HashSet<Definiendum>();
	private Point                     over;
	private Definiendum               referencedDefiniendum;
	private JScrollPane               scroll;
	private BufferedImage             image, thumbnail;
	private boolean                   overThumbnail;
	private FindToolBar               findToolBar           = new SearchToolBar(this);
	private JMenu                     searchMenu            = new JMenu("Search");
	private CollapsibleSectionPanel   csp;
	private FindDialog                findDialog;
	private ReplaceDialog             replaceDialog;
	private ReplaceToolBar            replaceToolBar        = new ReplaceToolBar(this);
	private Hashtable<String, String> links;
	private StatusBar                 statusBar;
	private Parser                    parser;
	private boolean                   dummyParse            = false;
	private ParseResult               parseResult           = null;
	private Vector<ActiveRegion>      activeRegions         = new Vector<ActiveRegion>();
	private ActiveRegion              activeRegion;
	private FocusableTip              zoomTip;

	public EditorTextArea(Console console, int rows, int cols, String language, StatusBar statusBar) {
		super(INSERT_MODE, false);
		super.setSize(rows, cols);
		this.statusBar = statusBar;
		this.console = console;
		setAutoIndentEnabled(true);
		setTabSize(2);
		convertTabsToSpaces();
		setTabsEmulated(true);
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setDismissDelay(10000);
		setUseFocusableTips(true);
		addKeyListener(this);
		setHyperlinksEnabled(true);
		setHyperlinkForeground(Color.blue);
		setLinkScanningMask(InputEvent.META_DOWN_MASK);
		addMouseMotionListener(this);
		addMouseListener(this);
		setFont(font);
		setBackground(background);
		setEditable(true);
		setForeground(Color.black);
		setBracketMatchingEnabled(true);
		setAnimateBracketMatching(true);
		setAutoIndentEnabled(true);
		setCloseCurlyBraces(true);
		setCloseMarkupTags(true);
		setClearWhitespaceLinesEnabled(true);
		zoomTip = new FocusableTip(this, null);

		int                       c    = JComponent.WHEN_IN_FOCUSED_WINDOW;
		int                       meta = InputEvent.META_MASK;

		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(language, "editor.XOCLTokens");
		setSyntaxEditingStyle(language);

		setCodeFoldingEnabled(true);
		setMarkOccurrences(true);

		setParserDelay(PARSE_DELAY);

		parser = new AbstractParser() {

			@Override
			public synchronized ParseResult parse(RSyntaxDocument doc, String s) {
				if (!dummyParse) {
					try {
						if (isDirty()) {
							// Highlights are added by various utilities. Remove them if the text has changed.
							removeAllLineHighlights();
						}
						Console.call("terminals", new Object[] { console.getLanguagePanel().getLanguageName() }, (tokens) -> XOCLTokens.setTokens((Vector<String>) tokens));
						parseResult = console.parse(file, parser, doc.getText(0, doc.getLength()));
						if (parseResult.getNotices().isEmpty()) {
							new Thread(() -> {
								try {
									console.registerLocations(parser, doc.getText(0, doc.getLength()));
								} catch (BadLocationException e) {
									e.printStackTrace();
								}
							}).start();
						}
						return parseResult;
					} catch (BadLocationException e) {
						throw new Error(e);
					}
				} else
					return parseResult;
			}
		};
		addParser(parser);
		setupSearchMenu();

		csp = new CollapsibleSectionPanel();
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, meta);
		Action    a  = csp.addBottomComponent(ks, findToolBar);
		getInputMap(c).put(ks, "PRESS_F");
		getActionMap().put("PRESS_F", a);
		a.putValue(Action.NAME, "Show Find Search Bar");
		searchMenu.add(new JMenuItem(a));
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int    x    = e.getX();
		int    y    = e.getY();
		int    pos  = viewToModel2D(new Point(x, y));
		String text = getText();
		if (pos >= 0 && pos < text.length()) {
			String url = getURLAt(text, pos);
			if (url != null) {
				try {
					URL    u    = new URL("file:" + url);
					String html = "<html><img src=\"" + u.toURI() + "\"></html>";
					zoomTip.toolTipRequested(e, html);
					return null;
				} catch (MalformedURLException | URISyntaxException e1) {
					e1.printStackTrace();
					return null;
				}
			} else
				return null;
		} else
			return null;
	}

	public FocusableTip getZoomTip() {
		return zoomTip;
	}

	private String getURLAt(String text, int pos) {
		int     start = pos;
		int     end   = pos;
		boolean found = false;
		while (start >= 0 && !found) {
			// Use file:// so that it shows as a clickable link.
			// Strip this so that appropriate prefix can be added elsewhere...
			if (text.startsWith("file://", start))
				found = true;
			else if (Character.isWhitespace(text.charAt(start)))
				break;
			else
				start--;
		}
		if (found) {
			found = false;
			while (end < text.length() && !found) {
				char c = text.charAt(end);
				if (!isURLChar(c)) {
					found = true;
				} else
					end++;
			}
			if (found)
				return text.substring(start, end).replace("file://", "");
			else
				return null;
		} else
			return null;
	}

	private boolean isURLChar(char c) {
		return Character.isDigit(c) || Character.isAlphabetic(c) || c == '.' || c == '/' || c == '_' || c == '-';
	}

	private void addJump(JPopupMenu menu) {
		JMenuItem jump = new JMenuItem("Jump to Def");
		menu.add(jump);
		jump.addActionListener((e) -> jumpToDef());
	}

	public void addLanguageMenuItem(String item) {
		languageMenuItemNames.add(item);
	}

	private void addLanguageMenuItemNames(JPopupMenu menu) {
		for (String path : languageMenuItemNames) {
			String[]  names = path.split("::");
			String    name  = names[names.length - 1];
			JMenuItem item  = new JMenuItem(name);
			item.addActionListener((e) -> Console.send("languageAction", new Object[] { path, this }));
			menu.add(item);
		}
	}

	private void addSetBackground(JPopupMenu menu) {
		JMenuItem setBackground = new JMenuItem("Set Background Colour");
		setBackground.addActionListener((ActionEvent ae) -> {
			setBackground();
		});
		menu.add(setBackground);
	}

	private void addZoom(JPopupMenu menu) {
		JMenuItem jmi1 = new JMenuItem("Zoom In");
		jmi1.addActionListener((ActionEvent ae) -> {
			zoomIn();
		});
		JMenuItem jmi2 = new JMenuItem("Zoom Out");
		jmi2.addActionListener((ActionEvent ae) -> {
			zoomOut();
		});
		JMenuItem jmi3 = new JMenuItem("Zoom Reset");
		jmi3.addActionListener((ActionEvent ae) -> {
			zoomReset();
		});
		jmi1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_MASK));
		jmi2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
		jmi3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_MASK));
		menu.add(jmi1);
		menu.add(jmi2);
		menu.add(jmi3);
	}

	@Override
	public JPopupMenu createPopupMenu() {
		JPopupMenu menu = super.createPopupMenu();
		addZoom(menu);
		addJump(menu);
		addSetBackground(menu);
		menu.add(searchMenu);
		addCopy(menu);
		addEval(menu);
		addLanguageMenuItemNames(menu);
		return menu;
	}

	private void addEval(JPopupMenu menu) {
		JMenu evaluate = new JMenu("Evaluate");
		menu.add(evaluate);
		JMenuItem file = new JMenuItem("File Contents");
		file.addActionListener((e) -> {
			console.compileAndLoad(this.file);
		});
		evaluate.add(file);
		JMenuItem selection = new JMenuItem("Selected Text");
		selection.addActionListener((e) -> {
			evalSelectedText();
		});
		evaluate.add(selection);
	}

	private void evaluate() {
		removeAllLineHighlights();
		int start = getSelectionStart();
		int end   = getSelectionEnd();
		if (start == end) {
			if (over != null) {
				String selection = getText().substring(over.x, over.y);
				console.eval(selection);
			}
		} else {
			String selection = getText().substring(start, end);
			console.eval(selection);
		}
	}

	private void evalSelectedText() {
		removeAllLineHighlights();
		int start = getSelectionStart();
		int end   = getSelectionEnd();
		if (start == end) {
			console.warning("Cannot evaluate. No selection.");
		} else {
			String selection = getText().substring(start, end);
			console.eval(selection);
		}
	}

	public void addCopy(JPopupMenu menu) {
		JMenu copy = new JMenu("Copy to Clipboard");
		menu.add(copy);
		JMenuItem visible = new JMenuItem("Visible Text");
		visible.addActionListener((e) -> {
			copyVisibleTextToClipboard();
		});
		copy.add(visible);
		JMenuItem selection = new JMenuItem("Selected Text");
		selection.addActionListener((e) -> {
			copySelectedTextToClipboard();
		});
		copy.add(selection);
	}

	private void copySelectedTextToClipboard() {
		removeAllLineHighlights();
		int start = getSelectionStart();
		int end   = getSelectionEnd();
		if (end > start) {
			try {
				int x0 = Integer.MAX_VALUE, y0 = Integer.MAX_VALUE;
				int x1 = Integer.MIN_VALUE, y1 = Integer.MIN_VALUE;
				for (int i = start; i < end; i++) {
					Rectangle r = modelToView(i);
					x0 = Math.min(x0, r.x);
					y0 = Math.min(y0, r.y);
					x1 = Math.max(x1, r.x);
					y1 = Math.max(y1, r.y);
				}
				y1 += getGraphics().getFontMetrics().getStringBounds("X", getGraphics()).getHeight();
				Rectangle     selection = new Rectangle(x0, y0, x1 - x0, y1 - y0);
				int           scale     = 4;
				BufferedImage image     = new BufferedImage(getWidth() * scale, getHeight() * scale, BufferedImage.TYPE_INT_RGB);
				Graphics2D    g         = (Graphics2D) image.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth() * scale, getHeight() * scale);
				g.scale(scale, scale);
				setSelectionStart(0);
				setSelectionEnd(0);
				paint(g);
				new ClipboardImage(image.getSubimage(selection.x * scale, selection.y * scale, selection.width * scale, selection.height * scale));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private void copyVisibleTextToClipboard() {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics      g     = image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		paint(image.getGraphics());
		new ClipboardImage(image);
	}

	public void deselectRange() {
	}

	@Override
	public synchronized List<ParserNotice> getParserNotices() {
		List<ParserNotice> notices    = super.getParserNotices();
		List<ParserNotice> newNotices = new Vector<ParserNotice>();
		newNotices.addAll(notices);
		for (Unbound u : unbound) {
			int line = getLine(u.getStart(), getText());
			newNotices.add(new DefaultParserNotice(parser, "Unbound: " + u.getName(), line));
		}
		return newNotices;
	}

	@Override
	public boolean getUnderlineForToken(Token t) {
		return super.getUnderlineForToken(t);
	}

	public void insertText(String text) {
		SwingUtilities.invokeLater(() -> {
			insert(text, getCaretPosition());
			repaint();
		});
	}

	private boolean isAbove(Point p1, Point p2) {
		return p1.getY() < p2.getY();
	}

	private boolean isBelow(Point p1, Point p2) {
		return p1.getY() > p2.getY();
	}

	private boolean isLeft(Point p1, Point p2) {
		return p1.getX() < p2.getX();
	}

	private boolean isOverThumbnail(int x, int y) {
		if (image != null) {
			Point p     = scroll.getViewport().getViewPosition();
			int   width = getWidth();
			return (x >= width - thumbnail.getWidth() && y <= p.y + thumbnail.getHeight());
		} else
			return false;
	}

	public void jumpToDef() {
	}

	private void jumpToDefInDifferentFile(Definiens d) {
		throw new Error("jump to def not implemented");
	}

	private void jumpToDefInSameFile(Definiens d) {
		try {
			int         start      = d.getLineStart();
			int         end        = d.getLineEnd();
			Rectangle2D startRect  = modelToView2D(start);
			Rectangle2D endRect    = modelToView2D(end);
			int         startx     = (int) startRect.getX();
			int         starty     = (int) startRect.getY();
			int         endx       = (int) endRect.getX();
			int         endy       = (int) endRect.getY();
			int         width      = Math.max(startx, endx) - Math.min(startx, endx);
			int         height     = Math.max(starty, endy) - Math.min(starty, endy);
			Rectangle   scrollRect = new Rectangle(startx, starty, width, height + 20);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollRectToVisible(scrollRect);
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == '-')
			zoomOut();
		if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == '=')
			zoomIn();
		if (e.isControlDown() && e.getKeyCode() == '0')
			zoomReset();
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
			try {
				save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if ((e.isControlDown() || e.isMetaDown()) && e.getKeyCode() == KeyEvent.VK_E) {
			evaluate();
		}
		if (e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_S) {
			try {
				save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_L) {
			console.compileAndLoad(getText(), file);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public synchronized void load(File file) {
		try {
			languageMenuItemNames.clear();
			statusBar.setFile(file);
			load(FileLocation.create(file), (String) null);
			this.file = file;
			if (caretPositions.containsKey(file.getAbsolutePath())) {
				setCaretPosition(caretPositions.get(file.getAbsolutePath()));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private int maxXPos(int start, int end) {
		// Find the maximum x position between the two text indices...
		try {
			int max = (int) modelToView2D(start).getX();
			for (int i = start; i <= end && i < getText().length(); i++) {
				if (getText().charAt(i) == '\n')
					max = (int) Math.max(max, modelToView2D(i).getX());
			}
			return max;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return 0;
		}
	}

	@Override
	public synchronized void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {

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
	public synchronized void mouseMoved(MouseEvent e) {
		over = null;
		int x     = e.getX();
		int y     = e.getY();
		int index = this.viewToModel(new Point(x, y));
		for (Point p : locations) {
			if (p.getX() <= index && p.getY() >= index) {
				if (over == null)
					over = p;
				else {
					if (p.getX() >= over.getX() && p.getY() <= over.getY())
						over = p;
				}
			}
			referencedDefiniendum = null;
			if (over != null) {
				for (Definiendum d : definiendum) {
					if (d.hasDefiniens(over)) {
						referencedDefiniendum = d;
					}
				}
			}
		}
		activeRegion = null;
		for (ActiveRegion ar : activeRegions) {
			if (ar.contains(index)) {
				activeRegion = ar;
			}
		}
		overThumbnail = isOverThumbnail(x, y);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	private void showToolTipDialog(String text, int x, int y) {
		System.err.println(text);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public synchronized void paint(Graphics g) {
		super.paint(g);
		showDiagramThumbnail(g);
		showUnbound(g);
		showActiveRegion(g);
		if (over != null) {
			try {
				Graphics2D g2 = (Graphics2D) g;
				Color      c  = g.getColor();
				Stroke     s  = g2.getStroke();
				g.setColor(Color.RED);
				g2.setStroke(new BasicStroke(2));
				Rectangle2D rStart = modelToView2D(over.x);
				Rectangle2D rEnd   = modelToView2D(over.y);
				int         height = g.getFontMetrics().getHeight();
				int         xStart = (int) (rStart.getX() - 0);
				int         yStart = (int) rStart.getY();
				int         xEnd   = (int) (rEnd.getX() + 0);
				int         yEnd   = (int) rEnd.getY();
				g.drawLine(xStart, yStart, xStart, yStart + height);
				g.drawLine(xStart, yStart, xStart + 2, yStart);
				g.drawLine(xStart, yStart + height, xStart + 2, yStart + height);
				g.drawLine(xEnd, yEnd, xEnd, yEnd + height);
				g.drawLine(xEnd, yEnd, xEnd - 2, yEnd);
				g.drawLine(xEnd, yEnd + height, xEnd - 2, yEnd + height);
				g.setColor(c);
				g2.setStroke(s);
				if (referencedDefiniendum != null) {
					Rectangle2D srcRect    = modelToView2D((int) referencedDefiniendum.getLineStart());
					Rectangle2D srcRectEnd = modelToView2D((int) referencedDefiniendum.getLineEnd());
					Rectangle2D tgtRect    = modelToView2D((int) referencedDefiniendum.getDefStart());
					Rectangle2D tgtRectEnd = modelToView2D((int) referencedDefiniendum.getDefEnd());
					int         srcx       = (int) srcRect.getX();
					int         srcy       = (int) srcRect.getY();
					int         srcw       = (int) ((int) srcRectEnd.getX() - srcRect.getX());
					int         srch       = (int) srcRect.getHeight();
					int         tgtw       = (int) (maxXPos((int) referencedDefiniendum.getDefStart(), (int) referencedDefiniendum.getDefEnd()) - tgtRect.getX());
					int         tgth       = (int) (tgtRectEnd.getY() == tgtRect.getY() ? tgtRect.getHeight() : (tgtRectEnd.getY() - tgtRect.getY()) + tgtRect.getHeight());
					int         tgtx       = (int) tgtRect.getX();
					int         tgty       = (int) tgtRect.getY();
					Point       midSrc     = new Point(srcx + (srcw / 2), srcy + (srch / 2));
					Point       midTgt     = new Point(tgtx + (tgtw / 2), tgty + (tgth / 2));
					if (isBelow(midTgt, midSrc)) {
						g2.setStroke(dashed);
						g.drawLine(srcx + (srcw / 2), srcy + srch, tgtx + (tgtw / 2), tgty);
						g2.setStroke(s);
						drawArrow(g, tgtx + (tgtw / 2), tgty, srcx + (srcw / 2), srcy + srch, false, 20.0, 10.0, g.getColor(), g.getColor());
					} else if (isAbove(midTgt, midSrc)) {
						g2.setStroke(dashed);
						g.drawLine(srcx + (srcw / 2), srcy, tgtx + (tgtw / 2), tgty + tgth);
						g2.setStroke(s);
						drawArrow(g, tgtx + (tgtw / 2), tgty + tgth, srcx + (srcw / 2), srcy, false, 20.0, 10.0, g.getColor(), g.getColor());
					} else if (isLeft(midTgt, midSrc)) {
						g2.setStroke(dashed);
						g.drawLine(srcx, srcy + (srch / 2), tgtx + tgtw, tgty + (tgth / 2));
						g2.setStroke(s);
						drawArrow(g, tgtx + tgtw, tgty + (tgth / 2), srcx, srcy + (srch / 2), false, 20.0, 10.0, g.getColor(), g.getColor());
					} else {
						g2.setStroke(dashed);
						g.drawLine(srcx + srcw, srcy + (srch / 2), tgtx, tgty + (tgth / 2));
						g2.setStroke(s);
						drawArrow(g, tgtx, tgty + (tgth / 2), srcx + srcw, srcy + (srch / 2), false, 20.0, 10.0, g.getColor(), g.getColor());
					}
					g.setColor(definiendumColour);
					g.fillRect(srcx, srcy, srcw, srch);
					g.setColor(definiensColour);
					g.fillRect(tgtx, tgty, tgtw, tgth);
					g.setColor(c);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private void showActiveRegion(Graphics g) {
		if (activeRegion != null) {
			activeRegion.highlight(g, this);
		}
	}

	public synchronized void registerDefiniendum(String name, int start, int end, int defStart, int defEnd) {
		definiendum.add(new Definiendum(name, start, end, defStart, defEnd));
	}

	/*
	 * 
	 * public synchronized void registerDiagram(String diagram, Hashtable<String, String> links) { OutputStream png = null; try { png = new FileOutputStream(new File("./init/diagram.png")); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } SourceStringReader reader = new SourceStringReader(diagram); try { String desc = reader.outputImage(png).getDescription(); if (desc != null) {
	 * try { image = ImageIO.read(new File("./init/diagram.png")); thumbnail = resize(image, (int) (getWidth() * 0.2), (int) (getHeight() * 0.1)); this.diagram = diagram; Hashtable<String, String>
	 * newLinks = new Hashtable<String, String>(); for (String link : links.keySet()) { newLinks.put(link, umlToSVG(links.get(link))); } this.links = newLinks; } catch (IOException e) {
	 * System.err.println(e); } } } catch (IOException e) { e.printStackTrace(); } }
	 * 
	 */

	public synchronized void registerLocation(int start, int end) {
		locations.add(new Point(start, end));
	}

	public synchronized void reset() {
		locations.clear();
		unbound.clear();
		definiendum.clear();
		definiendum.clear();
	}

	@Override
	public void save() throws IOException {
		if (file != null) {
			console.getXmfPanel().getFileSystem().clear(file);
		}
		super.save();
		if (!caretPositions.containsKey(file.getAbsolutePath())) {
			caretPositions.put(file.getAbsolutePath(), getCaretPosition());
		}
		statusBar.setDirty(false);
	}

	public void searchEvent(SearchEvent e) {
//MOVE TO NEXT LEVEL UP
		SearchEvent.Type type    = e.getType();
		SearchContext    context = e.getSearchContext();
		SearchResult     result  = null;

		switch (type) {
		default: // Prevent FindBugs warning later
		case MARK_ALL:
			result = SearchEngine.markAll(this, context);
			break;
		case FIND:
			result = SearchEngine.find(this, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(this);
			}
			break;
		case REPLACE:
			result = SearchEngine.replace(this, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(this);
			}
			break;
		case REPLACE_ALL:
			result = SearchEngine.replaceAll(this, context);
			JOptionPane.showMessageDialog(null, result.getCount() + " occurrences replaced.");
			break;
		}

		String text = null;
		if (result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		} else if (type == SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount() > 0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			} else {
				text = "";
			}
		} else {
			text = "Text not found";
		}
		// statusBar.setLabel(text);
	}

	public void selectRange(int start, int end) {
	}

	private void setBackground() {
		Color newColor = JColorChooser.showDialog(null, "Background Colour", getBackground());
		if (newColor != null)
			setBackground(newColor);
	}

	public void setScrollPane(JScrollPane scroll) {
		this.scroll = scroll;
		add(csp);
	}

	private void setupSearchMenu() {
		searchMenu.add(new JMenuItem(new ShowFindDialogAction()));
		searchMenu.add(new JMenuItem(new ShowReplaceDialogAction()));
		searchMenu.add(new JMenuItem(new GoToLineAction()));
		findDialog = new FindDialog((Dialog) getParent(), this);
		replaceDialog = new ReplaceDialog((Dialog) getParent(), this);
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

		// Create tool bars and tie their search contexts together also.
		findToolBar.setSearchContext(context);
		replaceToolBar.setSearchContext(context);
	}

	/*
	 * 
	 * private void showDiagram() { if (image != null) {
	 * 
	 * SourceStringReader reader = new SourceStringReader(diagram); final ByteArrayOutputStream os = new ByteArrayOutputStream(); try { String desc = reader.generateImage(os, new
	 * FileFormatOption(FileFormat.SVG)); os.close(); final String svg = new String(os.toByteArray(), Charset.forName("UTF-8")); new DiagramWindow(svg, links); } catch (Exception e) {
	 * e.printStackTrace(); } } }
	 */

	private void showDiagramThumbnail(Graphics g) {
		if (image != null) {
			Point p     = scroll.getViewport().getViewPosition();
			int   width = getWidth();
			g.drawImage(thumbnail, width - thumbnail.getWidth(), p.y, null);
			if (overThumbnail) {
				Color c = g.getColor();
				g.setColor(Color.red);
				g.drawRect(width - thumbnail.getWidth() - 1, p.y - 1, thumbnail.getWidth() + 1, thumbnail.getHeight() + 1);
				g.setColor(c);
			}
		}
	}

	private void showUnbound(Graphics g) {
		for (Unbound u : unbound) {
			try {
				int   height = g.getFontMetrics().getHeight();
				Color c      = g.getColor();
				g.setColor(UNBOUND);
				Rectangle2D rStart = modelToView2D(u.getStart());
				Rectangle2D rEnd   = modelToView2D(u.getEnd());
				int         startx = (int) rStart.getX();
				int         starty = (int) rStart.getY();
				int         endx   = (int) rEnd.getX();
				int         endy   = (int) rEnd.getY();
				g.fillRect(startx, starty, endx - startx, height);
				g.setColor(c);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private void startTimer() {
		if (task != null)
			task.cancel();
		task = new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tooltipText = null;
					}
				});
			}
		};
		timer.schedule(task, 3000);
	}

	private String umlToSVG(String uml) {
		SourceStringReader          reader = new SourceStringReader(uml);
		final ByteArrayOutputStream os     = new ByteArrayOutputStream();
		try {
			String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
			return new String(os.toByteArray(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public synchronized void unboundVar(String name, int start, int end) {
		unbound.add(new Unbound(name, start, end));
	}

	public void updateErrorStrip() {
		dummyParse = true;
		forceReparsing(parser);
		dummyParse = false;
	}

	public void zoom(int times) {
		for (int i = 0; i < times; i++) {
			zoomIn();
		}
	}

	private void zoomIn() {
		Font  font1 = getFont();
		float size  = font1.getSize2D() + FONT_INCREMENT_SIZE;
		if (size <= FONT_MAX_SIZE) {
			font1 = font1.deriveFont(size);
			setFont(font1);
		}
	}

	private void zoomOut() {
		Font  font1 = getFont();
		float size  = font1.getSize2D() - FONT_INCREMENT_SIZE;
		if (size >= FONT_MIN_SIZE) {
			font1 = font1.deriveFont(size);
			setFont(font1);
		}
	}

	private void zoomReset() {
		Font font1 = getFont();
		font1 = font1.deriveFont((float) DEFAULT_FONT_SIZE);
		setFont(font1);
	}

	public void registerResource(String name, int start, int end, JMenuItem item) {
		activeRegions.add(new Resource(start, end, item));
	}
}

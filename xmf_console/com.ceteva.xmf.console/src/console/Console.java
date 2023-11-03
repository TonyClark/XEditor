package console;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

import clients.ClientResult;
import diagrams.DiagramFrame;
import files.FileNode;
import files.FileSystem;
import inspect.InspectorManager;
import qwerky.tools.filesearch.FileSearchGUI;
import qwerky.tools.filesearch.SearchResponse;
import repl.CallConsumer;
import repl.ConsoleInput;
import repl.ConsoleOutput;
import repl.ConsoleTextArea;
import threads.ThreadInitiator;
import xos.ExampleMessageClient;
import xos.MessageClient;
import xos.OperatingSystem;

public class Console extends JFrame implements WindowListener {

	public static final int CONSOLE_WIDTH = 1500;
	public static final int CONSOLE_HEIGHT = 1000;
	public static final int CONSOLE_X = 500;
	public static final int CONSOLE_Y = 200;
	public static File SCRATCH = new File("xmf/scratch.xmf");
	public static String XMF_SRC = "";
	public static String XMF_INIT = "";
	public static Console CONSOLE = null;
	private static JFileChooser fileChooser = new JFileChooser();
	public static OperatingSystem XOS = new OperatingSystem();
	private static ConsoleInput consoleInput = new ConsoleInput();
	private static ConsoleTextArea editor = new ConsoleTextArea(consoleInput, 100, 500);
	private static Queue<Message> queue = new ArrayDeque<Message>();
	public static String[] XOSArgs = new String[] { "-port", "9999", "-image",
			"../com.ceteva.xmf.system/xmf-img/console.img", "-heapSize", "5000", "-freeHeap", "20", "-stackSize", "50",
			"-arg", "initfile:../com.ceteva.xmf.console/xmf/init.o" };

	public static void xmfInitialized() {
		// Called from XMF when it is up and running and ready to receive messages...
		run();
	}

	public static void browse(String path) {
		URI uri;
		try {
			uri = new URI("file:///" + path);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (URISyntaxException | IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void addMessage(String message, Object[] args, CallConsumer consumer) {
		synchronized (queue) {
			queue.add(new Message(message, args, (result) -> {
				if (consumer != null) {
					new Thread(() -> {
						consumer.consume(result);
					}).start();
				}
			}));
			queue.notifyAll();
		}
	}

	public static void run() {
		new Thread(() -> {
			try {
				while (true) {
					Message message = null;
					synchronized (queue) {
						// Important that the console is idle. This probably is only
						// required at startup when we are waiting for the initialisation
						// to complete....
						if (!queue.isEmpty() && allThreadsWaiting())
							message = queue.remove();
						else {
							queue.wait();
						}
					}
					if (message != null) {
						String name = message.getMessage();
						Object[] args = message.getArgs();
						CallConsumer consumer = message.getConsumer();
						new Thread(() -> {
							//System.err.println("Sending message threads = " + getThreadStates());
							XOS.getXVM().clientSend(name, args, new ThreadInitiator(), new ClientResult() {

								@Override
								public void error(String message) {
									System.err.println("Error: " + message);
									consumer.consume(consumer, message);
								}

								@Override
								public void result(Object value) {
									// System.err.println("Return: " + name + " = " + value);
									consumer.consume(consumer, value);
								}
							}, null);

						}).start();
						synchronized (consumer) {
							try {
								consumer.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Error: Console message handler crashed.");
				e.printStackTrace(System.err);
				System.err.println("Restarting console message handler....");
				run();
			}
		}).start();

	}

	private static boolean allThreadsWaiting() {
		threads.Thread start = XOS.getXVM().currentThread();
		threads.Thread t = start;
		do {
			if (t.stateToString().equals("ACTIVE"))
				return false;
			else
				t = t.next();
		} while (t != start);
		return true;
	}

	private static Vector<Object> getThreadStates() {
		threads.Thread start = XOS.getXVM().currentThread();
		Vector<Object> states = new Vector<Object>();
		threads.Thread t = start;
		do {
			states.add(t.stateToString());
			t = t.next();
		} while (t != start);
		return states;
	}

	public static void call(String handlerName, Object[] args, CallConsumer consumer) {

		// Synchronous call and return...

		// System.err.println("Call " + handlerName);

		addMessage(handlerName, args, consumer);
	}

	public static int getConsoleHeight() {
		return CONSOLE_HEIGHT;
	}

	public static int getConsoleWidth() {
		return CONSOLE_WIDTH;
	}

	public static int getConsoleX() {
		return CONSOLE_X;
	}

	public static int getConsoleY() {
		return CONSOLE_Y;
	}

	public static JFileChooser getFileChooser() {
		return fileChooser;
	}

	public static String getXMF_INIT() {
		return XMF_INIT;
	}

	public static String getXMF_SRC() {
		return XMF_SRC;
	}

	public static <T> String htmlList(Vector<T> values) {
		String html = "<html>";
		for (int i = 0; i < values.size(); i++) {
			html = html + values.get(i);
			if (i < values.size() - 1) {
				html = html + "<br>";
			}
		}
		return html + "</html";
	}

	public static void main(String[] args) {
		System.setOut(new PrintStream(new ConsoleOutput(editor)));
		System.setIn(consoleInput);
		new Thread(() -> {
			XOS.init(XOSArgs);
		}).start();
	}

	public static void send(String handlerName, Object[] args) {

		// Asynchronous send...

		// System.err.println("Send " + handlerName);

		addMessage(handlerName, args, (value) -> {
		});
	}

	private LanguagePanel languagePanel;
	private XMFPanel xmfPanel;
	private DiagramFrame diagramFrame = null;
	private FileSearchGUI fileSearch = new FileSearchGUI(this);
	private int scratchLength = 0;
	private FileTime scratchModified;

	public Console() {
		try {
			SwingUtilities.invokeAndWait(() -> {
				String[] args = new String[] { "../com.ceteva.xmf.system/xmf-src:xmf" };
				CONSOLE = this;
				XMFPanel x = new XMFPanel(this, editor, args[0]);
				setXmfPanel(x);
				setLayout(new BorderLayout());
				LanguagePanel l = new LanguagePanel(this);
				add(l, BorderLayout.NORTH);
				setLanguagePanel(l);
				add(x, BorderLayout.CENTER);
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				setSize(CONSOLE_WIDTH, CONSOLE_HEIGHT);
				setLocation(CONSOLE_X, CONSOLE_Y);
				setVisible(true);
				XMF_SRC = args[0].split(":")[0];
				XMF_INIT = args.length > 1 ? args[1] : "";
				JMenuBar bar = new JMenuBar();
				JMenu XMF = new JMenu("XMF");
				JMenuItem compile = new JMenuItem("Compile");
				compile.addActionListener((e) -> compileXMF());
				XMF.add(compile);
				JMenu touch = new JMenu("Touch");
				JMenuItem touchSource = new JMenuItem("Source");
				touchSource.addActionListener((e) -> touchSource());
				touch.add(touchSource);
				JMenuItem touchBinaries = new JMenuItem("Binaries");
				touchBinaries.addActionListener((e) -> touchBinaries());
				touch.add(touchBinaries);
				XMF.add(touch);
				JMenu document = new JMenu("Documentation");
				JMenuItem generate = new JMenuItem("Generate");
				generate.addActionListener((e) -> documentXMF());
				document.add(generate);
				addHTMLFiles(document, new File("./xmf-doc/Root"));
				XMF.add(document);
				JMenu build = new JMenu("Build");
				JMenuItem buildXMF = new JMenuItem("XMF");
				buildXMF.addActionListener((e) -> build("makexmf"));
				build.add(buildXMF);
				JMenuItem buildCompiler = new JMenuItem("Console");
				buildCompiler.addActionListener((e) -> build("makeconsole"));
				XMF.add(build);
				build.add(buildCompiler);
				JMenuItem diagrams = new JMenuItem("Diagrams");
				diagrams.addActionListener((e) -> diagrams(diagrams.getX(), diagrams.getY()));
				JMenu find = new JMenu("Find");
				JMenuItem findDef = new JMenuItem("Definition");
				XMF.add(find);
				find.add(findDef);
				findDef.addActionListener((e) -> findDef());
				JMenuItem inspector = new JMenuItem("Inspector");
				inspector.addActionListener((e) -> InspectorManager.MANAGER.setVisible(true));
				XMF.add(inspector);
				createScratch();
				XMF.add(diagrams);
				bar.add(Box.createHorizontalStrut(10));
				bar.add(new BackLabel(this));
				bar.add(Box.createHorizontalStrut(10));
				bar.add(new ForwardLabel(this));
				bar.add(Box.createHorizontalStrut(10));
				bar.add(XMF);
				setJMenuBar(bar);
				setTitle("XEditor");
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				CONSOLE.addWindowListener(this);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addChild(int parent, int child, String label) {
		xmfPanel.addChild(parent, child, label);
	}

	private void addHTMLFiles(JMenu document, File dir) {
		JMenu menu = new JMenu(dir.getName());
		document.add(menu);
		File[] files = dir.listFiles();
		if (files != null) {
			java.util.Arrays.sort(files, FileSystem::compareFiles);
			for (File file : dir.listFiles()) {
				if (file.getName().equals("index.html")) {
					JMenuItem doc = new JMenuItem("doc");
					menu.add(doc);
					doc.addActionListener((e) -> browse(dir.getAbsolutePath() + "/index.html"));
				}
			}
			for (File file : files) {
				addHTMLFiles(menu, file);
			}
		}
	}

	private void build(String builder) {
		call("getVersion", new Object[] {}, (version) -> {
			new Thread(() -> {
				String home = new File(".").getAbsolutePath();
				ProcessBuilder pb = new ProcessBuilder("/bin/sh", "bin/" + builder, ".", (String) version);
				pb.directory(new File(home + "/" + XMF_SRC));
				Process p;
				boolean[] alive = new boolean[] { true };
				System.out.println("[ " + builder + "]");
				try {
					p = pb.start();
					InputStream in = p.getInputStream();
					new Thread(() -> {
						while (alive[0]) {
							char c;
							try {
								c = (char) in.read();
								if (Character.isDefined(c)) {
									System.out.print(c);
									System.out.flush();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
					p.waitFor();
					alive[0] = false;
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		});
	}

	public void call(int target, String message, Object[] args, CallConsumer consume) {
		call("send", new Object[] { target, message, args }, consume);
	}

	protected void clearScratch() {
		try {
			Path file = Paths.get(SCRATCH.getAbsolutePath());
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
			FileTime modified = attr.lastModifiedTime();
			if (modified.compareTo(scratchModified) > 0) {
				int input = JOptionPane.showConfirmDialog(null, "Clear the scratch?", "Select an Option...",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (input == JOptionPane.OK_OPTION) {
					SCRATCH.delete();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void compileAndLoad(File file) {
		String language = getLanguage(file);
		if (language == null)
			language = "OCL::OCL";
		xmfPanel.compileAndLoad(file, language);
	}

	public void compileAndLoad(String text, File file) {
		String language = getLanguage(file);
		if (language == null)
			language = "OCL::OCL";
		xmfPanel.compileAndLoad(text, language);
	}

	public void compileAndLoadManifest(File file) {
		xmfPanel.compileAndLoadManifest(file);
	}

	private void compileXMF() {
		SwingUtilities.invokeLater(() -> {
			send("compileXMF", new Object[] { XMF_SRC });
		});
	}

	private void createScratch() {
		try {
			if (!SCRATCH.exists()) {
				SCRATCH.createNewFile();
			}
			Path file = Paths.get(SCRATCH.getAbsolutePath());
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
			scratchModified = attr.lastModifiedTime();
		} catch (IOException e1) {
			e1.printStackTrace();

		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				clearScratch();
			}
		});
	}

	private void diagrams(int x, int y) {
		if (diagramFrame == null) {
			SwingUtilities.invokeLater(() -> {
				Point pt = new Point(x, y);
				SwingUtilities.convertPointToScreen(pt, this);
				diagramFrame = new DiagramFrame(this);
				diagramFrame.setLocation(pt.x, pt.y - diagramFrame.getY());
			});
		} else
			diagramFrame.setVisible(true);
	}

	private void documentXMF() {
		SwingUtilities.invokeLater(() -> {
			send("documentXMF", new Object[] {});
		});
	}

	public void edit(SearchResponse response) {
		xmfPanel.edit(response);
	}

	public void eval(String text) {
		xmfPanel.eval(text);
	}

	private void findDef() {
		SwingUtilities.invokeLater(() -> {
			send("findDef", new Object[] {});
		});
	}

	public DiagramFrame getDiagramFrame() {
		return diagramFrame;
	}

	public FileSearchGUI getFileSearch() {
		return fileSearch;
	}

	public Object getFileSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getLanguage(File file) {
		String name = file.getName();
		if (name.endsWith(".xmf")) {
			name = name.replace(".xmf", "");
			if (name.contains(":")) {
				String[] parts = name.split(":", 2);
				return parts[1];
			}
		}
		return "OCL::OCL";
	}

	public LanguagePanel getLanguagePanel() {
		return languagePanel;
	}

	public XMFPanel getXmfPanel() {
		return xmfPanel;
	}

	public void load(File file) {
		String name = file.getName();
		String language = getLanguage(file);
		languagePanel.setLanguage(language);
		xmfPanel.getFileSystem().clear(file);
		xmfPanel.load(file);
		xmfPanel.addLanguageMenuItems(language);
	}

	public void loadPath(String path) {
		File file = new File(path);
		if (file.exists()) {
			load(file);
		} else
			throw new Error("cannot find file " + path);
	}

	public ParseResult parse(File file, Parser parser, String text) {
		if (xmfPanel == null)
			return null;
		else
			return xmfPanel.parse(file, parser, text, languagePanel.getLanguageName());
	}

	public void refreshBrowserNode(int id) {
		xmfPanel.refreshBrowserNode(id);
	}

	public void registerLocations(Parser parser, String text) {
		xmfPanel.registerLocations(parser, text, languagePanel.getLanguageName());
	}

	public void saveCaretPosition(FileNode node) {
		xmfPanel.saveCaretPosition(node);
	}

	public void send(int target, String message, Object[] args) {
		send("send", new Object[] { target, message, args });
	}

	public void setBrowserElementImage(int id, String imageFile) {
		xmfPanel.setBrowserElementImage(id, imageFile);
	}

	public void setBrowserHoverText(int id, String text) {
		xmfPanel.setBrowserHoverText(id, text);
	}

	public void setCaretFileNodePosition(FileNode selectedFile) {
		xmfPanel.setCaretPosition(selectedFile);
	}

	public void setCaretPosition(int pos) {
		xmfPanel.setCaretPosition(pos);
	}

	public void setLanguagePanel(LanguagePanel languagePanel) {
		this.languagePanel = languagePanel;
	}

	public void setRoot(Vector<Object> tree) {
		xmfPanel.setRoot(tree);
	}

	public void setXmfPanel(XMFPanel xmfPanel) {
		this.xmfPanel = xmfPanel;
	}

	private void touchBinaries() {
		SwingUtilities.invokeLater(() -> {
			send("touchXMF", new Object[] { XMF_SRC, false });
		});
	}

	private void touchSource() {
		SwingUtilities.invokeLater(() -> {
			send("touchXMF", new Object[] { XMF_SRC, true });
		});
	}

	public void warning(String message) {
		JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		send("exit", new Object[] { this });
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void exit() {
		System.exit(0);
	}

	public void browse() {
		int mode = fileChooser.getFileSelectionMode();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showDialog(this, "Browse");
		if (result == JFileChooser.APPROVE_OPTION) {
			File dir = fileChooser.getSelectedFile();
			xmfPanel.browseDir(dir);
		}
		fileChooser.setFileSelectionMode(mode);
	}

	public void handleNameResolution(Vector<String> names) {
		xmfPanel.handleNameResolution(names);
	}

}
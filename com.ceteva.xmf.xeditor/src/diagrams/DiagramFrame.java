package diagrams;

import java.awt.Point;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import console.Console;
import diagrams.filmstrips.Filmstrip;
import diagrams.model.Model;
import diagrams.model.ModelDiagramTab;
import tabs.TabLabel;
import web.SVG;

public class DiagramFrame extends JFrame {

	JButton                       button   = new JButton("Diagram");
	JTabbedPane                   tabs     = new JTabbedPane();
	Hashtable<String, DiagramTab> tabTable = new Hashtable<String, DiagramTab>();
	Console                       console;

	public DiagramFrame(Console console) {
		this.console = console;
		JMenuBar menuBar = new JMenuBar();
		JMenu diagram = new JMenu("Diagram");
		JMenuItem xmf = new JMenuItem("XMF");
		JMenuItem diagramSuppliers = new JMenuItem("Diagram Suppliers");
		JMenuItem filmstripSuppliers = new JMenuItem("Filmstrip Suppliers");
		JMenuItem plot = new JMenuItem("Plot");
		xmf.addActionListener((e) -> getDiagram());
		diagramSuppliers.addActionListener((e) -> getDiagramSuppliers());
		filmstripSuppliers.addActionListener((e) -> getFilmstrips());
		plot.addActionListener((e) -> getPlots());
		diagram.add(xmf);
		diagram.add(diagramSuppliers);
		diagram.add(filmstripSuppliers);
		diagram.add(plot);
		menuBar.add(diagram);
		setJMenuBar(menuBar);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		add(tabs);
		setSize(700, 700);
		setVisible(true);
	}

	private void getPlots() {
		Console.call("getPlots", new Object[] {}, (names) -> {
			SwingUtilities.invokeLater(() -> {
				Vector<Object> tree = new Vector<Object>();
				tree.add("Plots");
				for (String name : (Vector<String>) names) {
					Vector<String> child = new Vector<String>();
					child.add(name);
					tree.add(child);
				}
				String name = getPackagePath(button.getX(), button.getY(), tree);
				if (name != null) {
					if (tabTable.containsKey(name)) {
						tabs.setSelectedComponent(tabTable.get(name));
					} else {
						String[] path = name.split("::");
						String plotName = path[path.length - 1];
						Console.call("generatePlot", new Object[] { plotName }, (svg) -> {
							PlotTab plotTab = new PlotTab(console, plotName, (String) svg);
							tabTable.put(plotName, plotTab);
							tabs.addTab(plotName, plotTab);
							TabLabel.LabelAction delete = () -> delete(plotName);
							TabLabel.LabelAction select = () -> select(plotName);
							TabLabel label = new TabLabel(plotName, plotName, delete, select);
							tabs.setTabComponentAt(tabs.indexOfTab(plotName), label);
						});
					}
				}
			});
		});
	}

	public JTabbedPane getTabs() {
		return tabs;
	}

	private void getDiagram() {
		Console.call("getPackageStructure", new Object[] {}, (packages) -> {
			SwingUtilities.invokeLater(() -> {
				String name = getPackagePath(button.getX(), button.getY(), (Vector<Object>) packages);
				if (name != null) {
					if (tabTable.containsKey(name)) {
						tabs.setSelectedComponent(tabTable.get(name));
					} else {
						Console.call("getPackageModel", new Object[] { name }, (model) -> {
							showDiagram(name, (Model) model);
						});
					}
				}
			});
		});
	}

	protected void select(String name) {
		tabs.setSelectedIndex(tabs.indexOfTab(name));
	}

	private void delete(String key) {
		int i = tabs.indexOfTab(key);
		tabs.remove(i);
		tabTable.remove(key);
	}

	private void getDiagramSuppliers() {
		Console.call("getDiagramSuppliers", new Object[] {}, (pairs) -> {
			SwingUtilities.invokeLater(() -> {
				try {
					Vector<Object> names = new Vector<Object>();
					Vector<Object> diagrams = new Vector<Object>();
					Vector<Object> tree = new Vector<Object>();
					tree.add("Diagrams");
					for (Vector<Object> pair : (Vector<Vector<Object>>) pairs) {
						names.add("Diagrams::" + pair.get(0));
						diagrams.add(pair.get(1));
						Vector<Object> element = new Vector<Object>();
						element.add(pair.get(0));
						tree.add(element);
					}
					String name = getPackagePath(button.getX(), button.getY(), tree);
					if (name != null) {
						if (tabTable.containsKey(name)) {
							tabs.setSelectedComponent(tabTable.get(name));
						} else {
							int i = names.indexOf(name);
							Object object = diagrams.get(i);
							if (object instanceof Model) {
								Model model = (Model) object;
								showDiagram(name, model);
							} else {
								String diagram = (String) object;
								BasicDiagramTab tab = new BasicDiagramTab(console, this, name, SVG.plantUML2SVG(diagram));
								TabLabel.LabelAction delete = () -> delete(name);
								TabLabel.LabelAction select = () -> select(name);
								TabLabel label = new TabLabel(name, name, delete, select);
								tabTable.put(name, tab);
								tabs.addTab(name, tab);
								tabs.setTabComponentAt(tabs.indexOfTab(name), label);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}

	private void getFilmstrips() {
		Console.call("getFilmstrips", new Object[] {}, (names) -> {
			SwingUtilities.invokeLater(() -> {
				Vector<Object> tree = new Vector<Object>();
				tree.add("Filmstrips");
				for (String name : (Vector<String>) names) {
					Vector<String> child = new Vector<String>();
					child.add(name);
					tree.add(child);
				}
				String name = getPackagePath(button.getX(), button.getY(), tree);
				if (name != null) {
					if (tabTable.containsKey(name)) {
						tabs.setSelectedComponent(tabTable.get(name));
					} else {
						String[] path = name.split("::");
						Console.call("generateFilmstrip", new Object[] { path[path.length - 1] }, (filmstrip) -> {
							FilmstripTab tab = new FilmstripTab(console, this, name, (Filmstrip) filmstrip);
							TabLabel.LabelAction delete = () -> delete(name);
							TabLabel.LabelAction select = () -> select(name);
							TabLabel label = new TabLabel(name, name, delete, select);
							tabTable.put(name, tab);
							tabs.addTab(name, tab);
							tabs.setTabComponentAt(tabs.indexOfTab(name), label);
						});
					}
				}
			});
		});
	}

	private String getPackagePath(int x, int y, Vector<Object> packages) {
		PackageDialog dialog = new PackageDialog(packages);
		Point pt = new Point(x, y);
		SwingUtilities.convertPointToScreen(pt, this);
		dialog.setSize(200, 700);
		dialog.setLocation((int) pt.getX(), (int) pt.getY() - dialog.getHeight());
		dialog.setVisible(true);
		return dialog.getPath();
	}

	public void addTab(String title, DiagramTab tab) {
		if (tabTable.containsKey(title)) {
			tabs.remove(tabTable.get(title));
		}
		tabTable.put(title, tab);
		tabs.addTab(title, tab);
	}

	public void showDiagram(String name, Model model) {
		SwingUtilities.invokeLater(() -> {
			DiagramTab tab = new ModelDiagramTab(name, console, this, model);
			TabLabel.LabelAction delete = () -> delete(name);
			TabLabel.LabelAction select = () -> select(name);
			TabLabel label = new TabLabel(name, name, delete, select);
			tabTable.put(name, tab);
			tabs.addTab(name, tab);
			tabs.setTabComponentAt(tabs.indexOfTab(name), label);
			tabs.setSelectedIndex(tabs.indexOfTab(name));
		});
	}
}
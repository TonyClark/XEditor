package diagrams;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.LinkActivationEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherListener;

import console.Console;

public class PackageDiagramTab extends DiagramTab {

	private JMenuItem    hide              = new JMenuItem("Hide");
	private JMenuItem    show              = new JMenuItem("Show");
	private JMenuItem    reset             = new JMenuItem("Reset");
	private JMenu        toggle            = new JMenu("Toggle");
	private JMenuItem    toggle_attributes = new JMenuItem("Attributes");
	private JMenuItem    toggle_operations = new JMenuItem("Operations");
	private JMenuItem    includeXCore      = new JMenuItem("Include XCore");
	private JMenuItem    excludeXCore      = new JMenuItem("exclude XCore");
	private SVGPanel     svgPanel          = null;
	private Console      console           = null;
	private DiagramFrame frame             = null;
	private Descriptor   descriptor        = null;
	private boolean      isSelecting       = true;
	private boolean      includingXCore    = false;

	public PackageDiagramTab(Console console, DiagramFrame frame, String name) {
		this.console  = console;
		this.frame    = frame;
		this.svgPanel = new DiagramPanel(name,console) {
										@Override
										public void linkClicked(LinkActivationEvent l) {
											String url = l.getReferencedURI();
											String[] command = url.split(":");
											switch (command[0]) {
											case "SELECT":
												descriptor.select(command[1]);
												Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), includingXCore }, (pair) -> {
																				SwingUtilities.invokeLater(() -> {
																												Vector<Object> v = (Vector<Object>) pair;
																												descriptor.setSvg((String) v.get(0));
																												svgPanel.setSVG(descriptor.getSvg());
																											});
																			});
												break;
											}
										}

										public JPopupMenu popupMenu(MouseEvent e) {
											JPopupMenu menu = super.popupMenu(e);
											addDiagramItems(menu);
											return menu;
										}
									};
		setLayout(new BorderLayout());
		add(new JScrollPane(svgPanel), BorderLayout.CENTER);
		Console.call("getSVG", new Object[] { name, isSelecting, new Vector<String>(), new Vector<String>(), includingXCore }, (pair) -> {
			SwingUtilities.invokeLater(() -> {
				Vector<Object> v = (Vector<Object>) pair;
				descriptor = new Descriptor(name, (String) v.get(0), (Vector<String>) v.get(1));
				svgPanel.setSVG(descriptor.getSvg());
			});
		});
	}

	public void addDiagramItems(JPopupMenu menu) {
		menu.add(hide);
		menu.add(show);
		menu.add(reset);
		menu.add(toggle);
		menu.add(includingXCore ? excludeXCore : includeXCore);

		toggle.add(toggle_attributes);
		toggle.add(toggle_operations);

		hide.addActionListener((e) -> {
			descriptor.hide();
			Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), includingXCore }, (pair) -> {
				SwingUtilities.invokeLater(() -> {
					Vector<Object> v = (Vector<Object>) pair;
					descriptor.setSvg((String) v.get(0));
					svgPanel.setSVG(descriptor.getSvg());
				});
			});
		});

		show.addActionListener((e) -> {
			descriptor.show();
			Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), includingXCore }, (pair) -> {
				SwingUtilities.invokeLater(() -> {
					Vector<Object> v = (Vector<Object>) pair;
					descriptor.setSvg((String) v.get(0));
					svgPanel.setSVG(descriptor.getSvg());
				});
			});
		});

		reset.addActionListener((e) -> {
			descriptor.reset();
			repaint();
		});

		includeXCore.addActionListener((e) -> {
			Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), true }, (pair) -> {
				SwingUtilities.invokeLater(() -> {
					Vector<Object> v = (Vector<Object>) pair;
					descriptor.setSvg((String) v.get(0));
					svgPanel.setSVG(descriptor.getSvg());
					includingXCore = true;
				});
			});
		});

		excludeXCore.addActionListener((e) -> {
			Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), false }, (pair) -> {
				SwingUtilities.invokeLater(() -> {
					Vector<Object> v = (Vector<Object>) pair;
					descriptor.setSvg((String) v.get(0));
					svgPanel.setSVG(descriptor.getSvg());
					includingXCore = false;
				});
			});
		});

		toggle_operations.addActionListener((e) -> {
			Console.call("toggle", new Object[] {"OPERATIONS_ON_DIAGRAM"}, (result) -> {
				Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), includingXCore }, (pair) -> {
					SwingUtilities.invokeLater(() -> {
						Vector<Object> v = (Vector<Object>) pair;
						descriptor.setSvg((String) v.get(0));
						svgPanel.setSVG(descriptor.getSvg());
					});
				});
			});
		});

		toggle_attributes.addActionListener((e) -> {
			Console.call("toggle", new Object[] {"ATTRIBUTES_ON_DIAGRAM"}, (result) -> {
				Console.call("getSVG", new Object[] { descriptor.getName(), isSelecting, descriptor.getShown(), descriptor.getSelected(), includingXCore }, (pair) -> {
					SwingUtilities.invokeLater(() -> {
						Vector<Object> v = (Vector<Object>) pair;
						descriptor.setSvg((String) v.get(0));
						svgPanel.setSVG(descriptor.getSvg());
					});
				});
			});
		});

		// Set the JSVGCanvas listeners.
		svgPanel.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
			public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
				// label.setText("Document Loading...");
			}

			public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
				// label.setText("Document Loaded.");
			}
		});

		svgPanel.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
			public void gvtBuildStarted(GVTTreeBuilderEvent e) {
				// label.setText("Build Started...");
			}

			public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
				// label.setText("Build Done.");
			}
		});

		svgPanel.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
			public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
				// label.setText("Rendering Started...");
			}

			public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
				// label.setText("");
			}
		});

		svgPanel.addSVGLoadEventDispatcherListener(new SVGLoadEventDispatcherListener() {

			@Override
			public void svgLoadEventDispatchCancelled(SVGLoadEventDispatcherEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void svgLoadEventDispatchCompleted(SVGLoadEventDispatcherEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void svgLoadEventDispatchFailed(SVGLoadEventDispatcherEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent arg0) {
				// TODO Auto-generated method stub

			}
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
}
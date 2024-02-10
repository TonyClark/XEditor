package inspect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ObjectInspector extends Inspector implements MouseListener {

	private static final int NEAR = 50;

	public static ObjectInspector mkInspector(String snapshot, int objectHandle, String objectLabel, int classHandle,
			String classLabel, Vector<InspectorField> fields, boolean isClass, boolean satisfied, String report) {
		ObjectInspector inspector = (ObjectInspector) InspectorManager.MANAGER.getInspector(objectHandle, snapshot);
		if (inspector != null) {
			inspector.refresh(objectHandle, objectLabel, classHandle, classLabel, fields, satisfied, report);
			InspectorManager.MANAGER.setVisible(true);
			return inspector;
		} else {
			ObjectInspector i = new ObjectInspector(snapshot, objectHandle, objectLabel, classHandle, classLabel,
					fields, isClass, satisfied, report);
			int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
			int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
			i.setLocation(x, y - (i.getHeight() / 2));
			return i;
		}
	}

	private int objectHandle;
	private String objectLabel;
	private int classHandle;
	private String classLabel;
	private Vector<InspectorField> fields;
	private InspectorTable table;
	private InspectorBanner banner;

	public ObjectInspector(String snapshot, int objectHandle, String objectLabel, int classHandle, String classLabel,
			Vector<InspectorField> fields, boolean isClass, boolean satisfied, String report) {
		super(snapshot, objectHandle, objectLabel);
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		this.classHandle = classHandle;
		this.classLabel = classLabel;
		this.fields = fields;

		String[] columnNames = { "Slot Name", "Slot Value" };
		for (int field = 0; field < fields.size(); field++) {
			InspectorField f = fields.get(field);
			f.setInspector(this);
		}

		banner = new InspectorBanner(this, objectHandle, objectLabel, classHandle, classLabel, isClass, satisfied,
				report);

		JScrollPane scroll = new JScrollPane();
		table = new InspectorTable(this, fields, columnNames, scroll);
		scroll.getViewport().add(table);
		table.addMouseListener(this);
		setLayout(new BorderLayout());
		add(banner, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(scroll, BorderLayout.CENTER);
		scroll.setPreferredSize(table.getPreferredSize());
		add(panel, BorderLayout.CENTER);
		table.setFillsViewportHeight(true);
		table.setGridColor(Color.blue);
		setVisible(true);
		InspectorManager.MANAGER.addInspector(this, snapshot);
		putClientProperty("JInternalFrame.frameType", "normal");
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		pack();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			JTable source = (JTable) e.getSource();
			int row = source.rowAtPoint(e.getPoint());
			int column = source.columnAtPoint(e.getPoint());
			if (column == 1) {
				fields.get(row).rightClick(e);
			}
		}
	}

	private void inspect(int row) {
		InspectorField field = fields.get(row);
		field.inspect();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public int getObjectHandle() {
		return objectHandle;
	}

	public String getObjectLabel() {
		return objectLabel;
	}

	public int getClassHandle() {
		return classHandle;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public Vector<InspectorField> getFields() {
		return fields;
	}

	public void refresh(int objectHandle, String objectLabel, int classHandle, String classLabel,
			Vector<InspectorField> fields, boolean satisfied, String report) {
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		this.classHandle = classHandle;
		this.classLabel = classLabel;
		this.fields = fields;
		setTitle(objectLabel);
		banner.refresh(objectHandle, objectLabel, classHandle, classLabel, satisfied, report);
		table.refresh(fields);
	}

	public Vector<Edge> edges(Inspector target) {
		Vector<Edge> edges = new Vector<Edge>();
		for (InspectorField f : fields) {
			edges.addAll(f.edges(this, target));
		}
		return edges;
	}

}

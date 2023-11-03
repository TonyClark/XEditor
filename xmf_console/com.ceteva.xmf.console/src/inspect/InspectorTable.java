package inspect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

import console.Console;
import inspect.value.InspectorValue;

public class InspectorTable extends JTable {

	public static final Font HEADER_FONT = new Font("Monaco", Font.PLAIN, 12);
	public static final Font FONT = new Font("Monaco", Font.PLAIN, 14);

	private Vector<InspectorField> fields;
	private Inspector inspector;
	private InspectorModel model;

	public InspectorTable(Inspector inspector, Vector<InspectorField> fields, String[] columnNames,
			JScrollPane scrollPane) {
		super(new InspectorModel(fields, columnNames));
		this.fields = fields;
		this.inspector = inspector;
		this.model = (InspectorModel) getModel();
		setFont(FONT);
		getTableHeader().setFont(HEADER_FONT);
		getTableHeader().setBackground(Color.LIGHT_GRAY);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setDefaultRenderer(String.class, new CellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDropMode(DropMode.INSERT);
		setDragEnabled(true);
		setTransferHandler(new InspectorTransferHandler(this));
		setFillsViewportHeight(true);
		getColumnModel().getColumn(0).setMaxWidth(150);
		getColumnModel().getColumn(0).setMinWidth(150);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setRowHeight(16);
				Dimension p = getPreferredSize();
				Dimension v = scrollPane.getViewportBorderBounds().getSize();
				if (v.height > p.height) {
					int available = v.height - getRowCount() * getRowMargin();
					int perRow = available / getRowCount();
					setRowHeight(perRow);
				}
			}
		});
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column > 0)
			return fields.get(row).getCellEditor();
		else {
			return super.getCellEditor(row, column);
		}
	}

	public Vector<InspectorField> getFields() {
		return fields;
	}

	public Inspector getInspector() {
		return inspector;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// This occurs when a field is edited (drop should not come through here).
		// The new value should be in the editor in the field. It needs validating
		// and reverting to the original value if there is a problem...
		InspectorField field = fields.get(row);
		int handle = inspector.getObjectHandle();
		String name = field.getName();
		String xml = field.getValue().getXML();
		Console.call("provisionallySetSlot", new Object[] { handle, name, xml }, (values) -> {
			// The values are in the form [ok,value] where
			// ok states that the update can go ahead.
			// value is an inspector value
			Vector<Object> vs = (Vector<Object>) values;
			boolean ok = (boolean) vs.get(0);
			InspectorValue value = (InspectorValue) vs.get(1);
			if (ok) {
				field.setValue(value);
				super.setValueAt(value, row, column);
			} else
				field.getValue().undo();
		});
	}

	public void setDroppedCellValue(InspectorValue value, int row, int column) {
		super.setValueAt(value, row, column);
	}

	public void refresh(Vector<InspectorField> fields) {
		this.fields = fields;
		model.refresh(fields);
		invalidate();
	}

}

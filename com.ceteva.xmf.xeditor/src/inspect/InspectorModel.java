package inspect;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import inspect.value.InspectorValue;

public class InspectorModel implements TableModel {

	private Vector<InspectorField> fields;
	private String[] columnNames;
	private Vector<TableModelListener> listeners = new Vector<TableModelListener>();

	public InspectorModel(Vector<InspectorField> fields, String[] columnNames) {
		this.fields = fields;
		this.columnNames = columnNames;
	}

	@Override
	public int getRowCount() {
		return fields.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return fields.get(rowIndex).getName();
		} else
			return fields.get(rowIndex).getValue();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex > 0) {
			fields.get(rowIndex).setValue((InspectorValue) value);
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void refresh(Vector<InspectorField> fields) {
		this.fields = fields;
		fireTableChanged();
	}

	public void fireTableChanged() {
		for (TableModelListener l : listeners) {
			l.tableChanged(new TableModelEvent(this, 0, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
		}
	}

}

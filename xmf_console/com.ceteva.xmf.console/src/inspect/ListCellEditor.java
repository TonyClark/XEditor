package inspect;

import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import inspect.value.InspectorValue;

public class ListCellEditor extends DefaultCellEditor {

	private Vector<InspectorValue> values;
	private InspectorValue selected = null;
	private JTextField field;

	public ListCellEditor(Vector<InspectorValue> values) {
		super(new JTextField());
		this.values = values;
		field = (JTextField) getComponent();
		if (values.size() > 0) {
			selected = values.get(0);
			field.setText(selected.toString());
		}
		field.setEditable(false);
	}

	public void inspect() {
		if (selected != null) {
			selected.inspect();
		}
	}

	public String toString() {
		if (selected != null) {
			return selected.toString();
		} else {
			String s = "[";
			for (int i = 0; i < values.size(); i++) {
				s = s + values.get(i).toString();
				if (i + 1 < values.size()) {
					s = s + ",";
				}
			}
			return s + "]";
		}
	}

	public void selectAll() {
		selected = null;
		field.setText(toString());
		fireEditingStopped();
	}

	public void select(InspectorValue value) {
		selected = value;
		field.setText(value.toString());
		fireEditingStopped();
	}

	public InspectorValue getSelectedValue() {
		return selected;
	}

	public boolean isSelected() {
		return selected != null;
	}

}

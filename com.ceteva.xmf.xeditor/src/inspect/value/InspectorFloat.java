package inspect.value;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.undo.UndoManager;

import inspect.Edge;
import inspect.Inspector;

public class InspectorFloat extends InspectorValue {

	private TableCellEditor editor;
	private JTextField field = new JTextField();
	private UndoManager undoManager = new UndoManager();

	public InspectorFloat(double value) {
		editor = new DefaultCellEditor(field);
		field.setText(value + "");
		field.getDocument().addUndoableEditListener(undoManager);
	}

	@Override
	public String toString() {
		return field.getText();
	}

	@Override
	public TableCellEditor getCellEditor() {
		return editor;
	}

	@Override
	public void inspect() {

	}

	@Override
	public void rightClick(MouseEvent e, Inspector inspector) {

	}

	@Override
	public String getXML() {
		try {
			return "<Float value=\"" + Double.parseDouble(field.getText()) + "\"/>";
		} catch (Exception x) {
			return "<String value=\"" + field.getText() + "\"/>";
		}
	}

	@Override
	public void undo() {
		while (undoManager.canUndo()) {
			undoManager.undo();
		}
	}

	@Override
	public void addEdges(String name, Inspector source, Inspector target, Vector<Edge> edges) {
		
	}
}

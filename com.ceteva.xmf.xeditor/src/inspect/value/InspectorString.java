package inspect.value;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import inspect.Edge;
import inspect.Inspector;

public class InspectorString extends InspectorValue {

	private TableCellEditor editor;
	private JTextField field;
	private UndoManager undoManager = new UndoManager();

	public InspectorString(String label) {
		field = new JTextField(label);
		editor = new DefaultCellEditor(field);
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
		return "<String value=\"" + field.getText() + "\"/>";
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

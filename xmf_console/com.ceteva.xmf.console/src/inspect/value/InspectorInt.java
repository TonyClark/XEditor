package inspect.value;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.undo.UndoManager;

import inspect.Edge;
import inspect.Inspector;

public class InspectorInt extends InspectorValue implements ActionListener {

	private TableCellEditor editor;
	private JTextField field = new JTextField();
	private UndoManager undoManager = new UndoManager();

	public InspectorInt(int value) {
		editor = new DefaultCellEditor(field);
		field.setText(value + "");
		field.addActionListener(this);
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

	private void textChanged(ActionEvent e) {
		String text = field.getText();
		try {
			int i = Integer.parseInt(text);
		} catch (NumberFormatException x) {
			System.err.println(x);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		textChanged(e);
	}

	@Override
	public String getXML() {
		try {
			return "<Integer value=\"" + Integer.parseInt(field.getText()) + "\"/>";
		} catch (Exception x) {
			return "<String value=\"" + field.getText() + "\"/>";
		}
	}

	@Override
	public void undo() {
		while(undoManager.canUndo()) {
			undoManager.undo();
		}
	}

	@Override
	public void addEdges(String name, Inspector source, Inspector target, Vector<Edge> edges) {
		
	}
}

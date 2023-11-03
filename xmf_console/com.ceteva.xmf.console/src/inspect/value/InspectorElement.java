package inspect.value;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import console.Console;
import inspect.Edge;
import inspect.Inspector;
import inspect.InspectorManager;

public class InspectorElement extends InspectorValue {

	private int handle;
	private TableCellEditor editor;
	private JTextField textField;
	private Vector<String> operations;

	public InspectorElement(int handle, String label, Vector<String> operations) {
		super();
		this.handle = handle;
		this.operations = operations;
		textField = new JTextField(label);
		editor = new DefaultCellEditor(textField);
		textField.setEditable(false);
	}

	@Override
	public String toString() {
		return textField.getText();
	}

	@Override
	public TableCellEditor getCellEditor() {
		return editor;
	}

	@Override
	public void inspect() {
		String snapshot = InspectorManager.MANAGER.currentSnapshotName();
		Console.CONSOLE.send(handle, "edit", new Object[] {snapshot});
	}

	@Override
	public void rightClick(MouseEvent e, Inspector inspector) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem inspect = new JMenuItem("Inspect");
		inspect.addActionListener((x) -> inspect());
		popup.add(inspect);
		JMenu ops = new JMenu("Operations");
		for (String name : operations) {
			JMenuItem i = new JMenuItem(name);
			i.addActionListener((x) -> invoke(name));
			ops.add(i);
		}
		popup.add(ops);
		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	private void invoke(String name) {
		String snapshot = InspectorManager.MANAGER.currentSnapshotName();
		Console.CONSOLE.send("invokeThenEdit", new Object[] { handle, name, new Vector<Object>(),snapshot });
	}

	@Override
	public String getXML() {
		return "<Element handle=\"" + handle + "\"/>";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEdges(String name, Inspector source, Inspector target, Vector<Edge> edges) {
		if (target.getObjectHandle() == handle) {
			edges.add(new Edge(name, source, target));
		}
	}

}

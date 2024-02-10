package inspect.value;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellEditor;

import inspect.Edge;
import inspect.Inspector;
import inspect.ListCellEditor;

public class InspectorList extends InspectorValue {

	private Vector<InspectorValue> values;
	private ListCellEditor editor = null;

	public InspectorList(Vector<InspectorValue> values) {
		super();
		this.values = values;
		this.editor = new ListCellEditor(values);
	}

	@Override
	public String toString() {
		return editor.toString();
	}

	@Override
	public TableCellEditor getCellEditor() {
		return editor;
	}

	@Override
	public void inspect() {
		if (editor != null) {
			editor.inspect();
		}
	}

	@Override
	public void rightClick(MouseEvent e, Inspector inspector) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem inspect = new JMenuItem("Inspect");
		inspect.addActionListener((x) -> inspect());
		popup.add(inspect);
		JMenu select = new JMenu("Select");
		popup.add(select);
		JMenuItem all = new JMenuItem("All");
		select.add(all);
		all.addActionListener((x) -> editor.selectAll());
		for (InspectorValue value : values) {
			JMenuItem i = new JMenuItem(value.toString());
			i.addActionListener((x) -> editor.select(value));
			select.add(i);
		}
		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public String getXML() {
		String xml = "<List>";
		for (InspectorValue value : values) {
			xml += value.getXML();
		}
		return xml + "</List>";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

	public InspectorValue getSelectedValue() {
		if (editor.isSelected())
			return editor.getSelectedValue();
		else
			return this;
	}

	@Override
	public void addEdges(String name, Inspector source, Inspector target, Vector<Edge> edges) {
		for(InspectorValue value : values) {
			value.addEdges(name,source, target, edges);
		}
	}

}

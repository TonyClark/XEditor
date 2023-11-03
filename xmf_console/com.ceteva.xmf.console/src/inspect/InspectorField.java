package inspect;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.table.TableCellEditor;

import inspect.value.InspectorValue;

public class InspectorField {

	private String name;
	private InspectorValue value;
	private Inspector inspector;

	public InspectorField(String name, InspectorValue value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public InspectorValue getValue() {
		return value;
	}

	public TableCellEditor getCellEditor() {
		return value.getCellEditor();
	}

	public void inspect() {
		value.inspect();
	}

	public void rightClick(MouseEvent e) {
		value.rightClick(e, inspector);
	}

	public void setInspector(Inspector inspector) {
		this.inspector = inspector;
	}

	@Override
	public String toString() {
		return "InspectorField [name=" + name + ", value=" + value + "]";
	}

	public void setValue(InspectorValue value) {
		this.value = value;
	}

	public Vector<Edge> edges(Inspector source, Inspector target) {
		Vector<Edge> edges = new Vector<Edge>();
		value.addEdges(name, source, target, edges);
		return edges;
	}

}

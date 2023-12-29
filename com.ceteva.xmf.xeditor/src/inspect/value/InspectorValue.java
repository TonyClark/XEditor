package inspect.value;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.table.TableCellEditor;

import inspect.Edge;
import inspect.Inspector;

public abstract class InspectorValue {

	public abstract TableCellEditor getCellEditor();

	public abstract void inspect();

	public abstract void rightClick(MouseEvent e, Inspector inspector);

	public abstract String getXML();

	public abstract void undo();

	public abstract void addEdges(String name, Inspector source, Inspector target, Vector<Edge> edges);
}

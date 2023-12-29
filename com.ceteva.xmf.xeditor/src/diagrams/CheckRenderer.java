package diagrams;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class CheckRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		boolean checked = (boolean) table.getValueAt(row, column);
		c.setBackground(checked ? Color.green : Color.red);
		return c;
	}

}

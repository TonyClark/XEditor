package diagrams;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import diagrams.filmstrips.Filmstrip;

public class CheckPanel extends JTable {

	public CheckPanel(Filmstrip filmstrip) {
		super(checkRows(filmstrip.getChecks()), new Object[] { "Constraint", "Is Satisfied" });
    DefaultTableModel model = new DefaultTableModel(filmstrip.getChecks().size(), 2) {
      private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int row, int column) {
				return filmstrip.getChecks().get(row).get(column);
			}

			@Override
      public Class<?> getColumnClass(int columnIndex) {
         return columnIndex == 1 ? String.class: Object.class;
      }

			@Override
			public String getColumnName(int column) {
				return column == 0 ? "Constraint Name" : "Is Satisfied";
			}
   };
		setModel(model);
		setDefaultRenderer(String.class,new CheckRenderer());
		getTableHeader().setOpaque(false);
		getTableHeader().setBackground(Color.lightGray);
	}

	private static Object[][] checkRows(Vector<Vector<String>> checks) {
		Object[][] rows = new Object[checks.size()][2];
		for (int i = 0; i < checks.size(); i++) {
			Vector<String> check = checks.get(i);
			rows[i][0] = check.get(0);
			rows[i][1] = check.get(1);
		}
		return rows;
	}

}

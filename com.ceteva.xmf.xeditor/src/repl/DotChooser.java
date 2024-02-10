package repl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import console.Console;

public class DotChooser extends JDialog {

	private String selectedName = "";

	public String getSelectedName() {
		return selectedName;
	}

	public DotChooser(Console console, Vector<String> fieldNames, Vector<String> fieldTypes, Vector<String> opNames, Vector<String> opTypes) {
		super(console, "Dot", true);
		setSize(new Dimension(500, 500));
		JPanel lists = new JPanel();
		lists.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		Vector<String> names         = new Vector<String>();
		Vector<String> ops           = new Vector<String>();
		int            maxNameLength = 0;
		int            maxOpLength   = 0;
		for (int i = 0; i < fieldNames.size(); i++) {
			maxNameLength = Math.max(maxNameLength, fieldNames.get(i).length());
		}
		maxNameLength++;
		for (int i = 0; i < opNames.size(); i++) {
			maxOpLength = Math.max(maxOpLength, opNames.get(i).length());
		}
		maxOpLength++;
		for (int i = 0; i < fieldNames.size(); i++) {
			String name = fieldNames.get(i);
			name = String.format("%-" + maxNameLength + "s", name);
			String type = fieldTypes.get(i);
			type = type.replace("Root::XCore::", "");
			type = type.replace("Root::", "");
			names.add(name + "::" + type);
		}
		for (int i = 0; i < opNames.size(); i++) {
			String name = opNames.get(i);
			name = String.format("%-" + maxOpLength + "s", name);
			String type = opTypes.get(i);
			type = type.replace("Root::XCore::", "");
			type = type.replace("Root::", "");
			ops.add(name + "::" + type);
		}
		Collections.sort(names);
		Collections.sort(ops);
		JList<String> fields     = new JList<String>(names);
		JList<String> operations = new JList<String>(ops);
		fields.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int i = fields.getSelectedIndex();
					if (i >= 0) {
						Collections.sort(fieldNames);
						selectedName = fieldNames.get(i);
					}
					setVisible(false);
				} else if (evt.getClickCount() == 3) {

					// Triple-click detected
					int index = list.locationToIndex(evt.getPoint());
				}
			}
		});
		operations.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int i = operations.getSelectedIndex();
					if (i >= 0) {
						Collections.sort(opNames);
						selectedName = opNames.get(i);
					}
					setVisible(false);
				} else if (evt.getClickCount() == 3) {

					// Triple-click detected
					int index = list.locationToIndex(evt.getPoint());
				}
			}
		});
		fields.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		operations.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		lists.add(new JScrollPane(fields), BorderLayout.WEST);
		lists.add(new JScrollPane(operations), BorderLayout.EAST);
		add(lists, BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		add(buttonPanel, BorderLayout.SOUTH);
		JButton b = new JButton("Select");
		b.addActionListener((e) -> {
			int i = fields.getSelectedIndex();
			if (i >= 0) {
				Collections.sort(fieldNames);
				selectedName = fieldNames.get(i);
			} else {
				i = operations.getSelectedIndex();
				if (i >= 0) {
					Collections.sort(opNames);
					selectedName = opNames.get(i);
				}

			}
			setVisible(false);
		});
		buttonPanel.add(b);
		KeyStroke      stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener action = new ActionListener() {
														@Override
														public void actionPerformed(ActionEvent e) {
															setVisible(false);
															dispose();
														}                                             // end of actionPerformed
													};
		getRootPane().registerKeyboardAction(action, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
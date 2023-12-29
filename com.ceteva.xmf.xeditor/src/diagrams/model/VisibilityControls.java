package diagrams.model;

import java.awt.Color;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class VisibilityControls extends JPanel {

	public static Image a = new ImageIcon("icons/attribute.png").getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
	public static Image c = new ImageIcon("icons/class.png").getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
	public static Image p = new ImageIcon("icons/package.png").getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
	public static Image o = new ImageIcon("icons/operation.png").getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);

	public VisibilityControls(VisibilitySelection visibility) {
		JToggleButton attributes = new JToggleButton(new ImageIcon(a), true);
		JToggleButton classes = new JToggleButton(new ImageIcon(c), true);
		JToggleButton packages = new JToggleButton(new ImageIcon(p), true);
		JToggleButton operations = new JToggleButton(new ImageIcon(o), true);
		add(packages);
		add(classes);
		add(attributes);
		add(operations);
		attributes.addActionListener((e) -> {
			visibility.toggleAttributes(attributes.isSelected());
			if(!attributes.isSelected())
				attributes.setBackground(Color.black);
			else attributes.setBackground(Color.white);
		});
		classes.addActionListener((e) -> {
			visibility.toggleClasses(classes.isSelected());
			if(!classes.isSelected())
				classes.setBackground(Color.black);
			else classes.setBackground(Color.white);
		});
		packages.addActionListener((e) -> {
			visibility.togglePackages(packages.isSelected());
			if(!packages.isSelected())
				packages.setBackground(Color.black);
			else packages.setBackground(Color.white);
		});
		operations.addActionListener((e) -> {
			visibility.toggleOperations(operations.isSelected());
			if(!operations.isSelected())
				operations.setBackground(Color.black);
			else operations.setBackground(Color.white);
		});
	}

}

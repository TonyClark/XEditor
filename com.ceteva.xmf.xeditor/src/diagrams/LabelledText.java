package diagrams;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LabelledText extends JPanel {

	private String label;
	private JTextField text = new JTextField();

	public LabelledText(String label) {
		this.label = label;
		setLayout(new BorderLayout());
		add(new JLabel(label),BorderLayout.WEST);
		add(text,BorderLayout.CENTER);
	}

	public void setText(String message) {
		text.setText(message);
	}

}

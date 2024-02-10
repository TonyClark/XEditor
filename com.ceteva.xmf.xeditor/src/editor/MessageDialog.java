package editor;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class MessageDialog extends JDialog {

	private final JScrollPane scrollPane = new JScrollPane();
	private JTextPane text = new JTextPane();

	public MessageDialog(String messages) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		text.setText(messages);
		scrollPane.setViewportView(text);
		setVisible(true);
	}
}
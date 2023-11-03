package console;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LanguagePanel extends JPanel {

	private static final String defaultLanguage = "OCL::OCL";
	private static final String defaultNT = "CompilationUnit";

	private Console console;
	private JLabel language = new JLabel("language");
	private JTextField languageName = new JTextField(defaultLanguage);

	public LanguagePanel(Console console) {
		this.console = console;
		setLayout(new BorderLayout());
		JPanel languagePanel = new JPanel();
		languageName.setColumns(20);
		languagePanel.add(language);
		languagePanel.add(languageName);
		add(languagePanel,BorderLayout.WEST);
		JPanel ntPanel = new JPanel();
		add(ntPanel,BorderLayout.CENTER);
		JButton reset = new JButton("reset");
		reset.addActionListener((e) -> reset());
		add(reset,BorderLayout.EAST);
	}

	private void reset() {
		languageName.setText(defaultLanguage);
	}

	public String getLanguageName() {
		return languageName.getText();
	}

	public void setLanguage(String language) {
		languageName.setText(language);
	}

}

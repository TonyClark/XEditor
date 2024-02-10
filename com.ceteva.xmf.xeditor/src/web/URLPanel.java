package web;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class URLPanel extends JPanel implements ActionListener {

	private JTextField location = new JTextField(200);
	private BrowserPanel browser;

	public URLPanel(BrowserPanel browser) {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		JLabel label = new JLabel("location");
		add(label);
		add(location);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		location.addActionListener(this);
		this.browser = browser;
	}

	public void setLocation(String l) {
		location.setText(l);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String url = location.getText();
		browser.setURL(url);
	}

}

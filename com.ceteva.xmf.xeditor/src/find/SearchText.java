package find;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SearchText extends JPanel implements DocumentListener {

	private Finder     finder;
	private JTextField search   = new JTextField();
	private JLabel     label    = new JLabel("Search:");
	private JCheckBox  isRegExp = new JCheckBox("regexp", false);

	public SearchText(Finder finder, String instructions) {
		this.finder = finder;
		setLayout(new BorderLayout());
		add(new JScrollPane(new Instructions(instructions)), BorderLayout.NORTH);
		add(label, BorderLayout.WEST);
		add(search, BorderLayout.CENTER);
		add(isRegExp, BorderLayout.EAST);
		search.getDocument().addDocumentListener(this);
		isRegExp.addActionListener((e) -> update());
		setBorder(BorderFactory.createRaisedBevelBorder());
	}

	public void update() {
		String query = search.getText();
		if (query.length() > 0) {
			Vector<TreeItem> items = finder.search(query, isRegExp.isSelected());
			finder.setSearchResults(items);
		} else
			finder.clearSearchResults();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		update();
	}

	public void select() {
		SwingUtilities.invokeLater(() -> search.requestFocus());
	}

}

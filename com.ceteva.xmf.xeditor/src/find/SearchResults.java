package find;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SearchResults extends JPanel implements MouseListener {

	private Finder                   finder;
	private DefaultListModel<Result> model   = new DefaultListModel<Result>();
	private JList                    results = new JList(model);
	JLabel                           label   = new JLabel("---Results---");

	public SearchResults(Finder finder) {
		this.finder = finder;
		setLayout(new BorderLayout());
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, BorderLayout.NORTH);
		add(new JScrollPane(results), BorderLayout.CENTER);
		results.setFont(Finder.FONT);
		label.setFont(Finder.FONT);
		setBorder(BorderFactory.createRaisedBevelBorder());
		results.addMouseListener(this);
	}

	public void reset() {
		model.removeAllElements();
	}

	public void add(TreeItem item) {
		if (!model.contains(item)) {
			Result result = new Result(item);
			model.addElement(result);
			ArrayList<Result> results = new ArrayList<Result>();

			for (int i = 0; i < model.getSize(); i++) {
				results.add(model.get(i));
			}
			Collections.sort(results, (r1, r2) -> r1.compare(r2));
			model.removeAllElements();
			for (Result r : results)
				model.addElement(r);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Result result = (Result) results.getSelectedValue();
		if (result != null) {
			result.getItem().select(finder);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

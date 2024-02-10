package editor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.fife.rsta.ui.search.FindToolBar;
import org.fife.rsta.ui.search.SearchListener;

public class SearchToolBar extends FindToolBar implements ActionListener {

	public SearchToolBar(SearchListener listener) {
		super(listener);
	}

	@Override
	public Container createButtonPanel() {
		Container buttons = super.createButtonPanel();
		JButton files = new JButton("Files");
		buttons.add(files);
		files.addActionListener(this);
		return buttons;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String text = getFindText();
		if (text != null && text.length() > 0) {
			System.out.println("SEARCH");
		}
	}

}

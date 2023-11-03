package find;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import console.Console;
import repl.CallConsumer;

public class Finder extends JFrame implements WindowListener {

	public static Font    FONT         = new Font(Font.MONOSPACED, Font.PLAIN, 10);

	private String        instructions = "";
	private SearchText    search       = null;
	private SearchResults results      = new SearchResults(this);
	private SearchTree    tree         = new SearchTree(this);
	private int           closeHandler;

	public Finder(String instructions, int closeHandler) {
		this.instructions = instructions;
		this.closeHandler = closeHandler;
		this.search = new SearchText(this, instructions);
		setTitle("Find");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		setBounds(100, 100, 450, 300);
		setLayout(new BorderLayout());
		add(search, BorderLayout.NORTH);
		add(results, BorderLayout.CENTER);
		add(tree, BorderLayout.SOUTH);
		addEscapeHandler();
		pack();
	}
	
	
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		search.select();
	}



	private void addEscapeHandler() {
		KeyStroke      stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener action = new ActionListener() {
														@Override
														public void actionPerformed(ActionEvent e) {
															setVisible(false);
															dispose();
														}
													};
		getRootPane().registerKeyboardAction(action, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public void addSearchResult(TreeItem item) {
		results.add(item);
	}

	public void call(int handler, String message, Object[] args, CallConsumer consumer) {
		Console.CONSOLE.call(handler, message, args, consumer);
	}

	public void clearSearchResults() {
		results.reset();
		tree.collapseAll();
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public void closeEntry() {
		tree.closeEntry();
	}

	public void expandAll() {
		tree.expandAll();
	}

	public void collapseAll() {
		tree.collapseAll();
	}

	public void openEntry(String marker, String id, String label, int doubleClick, ItemMenu menu) {
		tree.openEntry(marker, id, label, doubleClick, menu);
	}

	public Vector<TreeItem> search(String query, boolean isRegExp) {
		return tree.search(query, isRegExp);
	}

	private void select(Vector<TreeItem> items) {
		tree.select(items);
	}

	public void send(int handler, String message, Object... args) {
		Console.CONSOLE.send(handler, message, args);
	}

	public void setSearchResults(Vector<TreeItem> items) {
		results.reset();
		tree.collapsePath();
		for (TreeItem item : items) {
			results.add(item);
		}
		select(items);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		send(closeHandler, "closed");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}

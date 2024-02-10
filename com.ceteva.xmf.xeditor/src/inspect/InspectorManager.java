package inspect;

import java.awt.BorderLayout;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import console.Console;

public class InspectorManager extends JFrame {

	public static InspectorManager MANAGER = new InspectorManager();

	private Hashtable<String, InspectorDesktop> desktops = new Hashtable<String, InspectorDesktop>();
	private JTabbedPane snapshots = new JTabbedPane();

	public InspectorManager() {
		setTitle("Snapshots");
		setLayout(new BorderLayout());
		add(snapshots, BorderLayout.CENTER);
		setSize(Console.CONSOLE.getWidth(), Console.CONSOLE.getHeight());
		setLocation(Console.CONSOLE.getX(), Console.CONSOLE.getY());
		setVisible(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	public void addSnapshot(String name) {
		InspectorDesktop snapshot = new InspectorDesktop();
		desktops.put(name,snapshot);
		snapshots.addTab(name, new JScrollPane(snapshot));
	}

	public Inspector getInspector(int objectHandle, String name) {
		if (desktops.containsKey(name)) {
			InspectorDesktop snapshot = desktops.get(name);
			return snapshot.getSnapshot(objectHandle);
		} else {
			return null;
		}
	}

	public void addInspector(Inspector inspector, String name) {
		setVisible(true);
		if (desktops.containsKey(name)) {
			InspectorDesktop snapshot = desktops.get(name);
			snapshot.addInspector(inspector);
		} else {
			addSnapshot(name);
			addInspector(inspector, name);
		}
	}

	public void removeInspector(Inspector inspector, String name) {
		if (desktops.containsKey(name)) {
			InspectorDesktop snapshot = desktops.get(name);
			snapshot.removeInspector(inspector);
		} else {
			addSnapshot(name);
			removeInspector(inspector, name);
		}
	}

	public String currentSnapshotName() {
		int selectedIndex = snapshots.getSelectedIndex();
		return snapshots.getTitleAt(selectedIndex);
	}

}

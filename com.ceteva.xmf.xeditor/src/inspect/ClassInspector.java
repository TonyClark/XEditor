package inspect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ClassInspector extends Inspector implements MouseListener {

	private static final int NEAR = 50;

	public static ClassInspector mkInspector(String snapshot, int classHandle, String classLabel, boolean satisfied, String report) {
		ClassInspector inspector = (ClassInspector) InspectorManager.MANAGER.getInspector(classHandle, snapshot);
		if (inspector != null) {
			// inspector.refresh(classHandle, classLabel, classHandle, classLabel, fields, satisfied, report);
			InspectorManager.MANAGER.setVisible(true);
			return inspector;
		} else {
			ClassInspector i = new ClassInspector(snapshot, classHandle, classLabel, satisfied, report);
			int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
			int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
			i.setLocation(x, y - (i.getHeight() / 2));
			return i;
		}
	}

	private InspectorBanner banner;

	public ClassInspector(String snapshot, int classHandle, String classLabel, boolean satisfied, String report) {
		super(snapshot, classHandle, classLabel);

		// banner = new InspectorBanner(this, objectHandle, objectLabel, classHandle, classLabel, isClass, satisfied, report);

		setVisible(true);
		InspectorManager.MANAGER.addInspector(this, snapshot);
		putClientProperty("JInternalFrame.frameType", "normal");
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		pack();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void refresh(int objectHandle, String objectLabel, int classHandle, String classLabel, Vector<InspectorField> fields, boolean satisfied, String report) {

	}

	public Vector<Edge> edges(Inspector target) {
		Vector<Edge> edges = new Vector<Edge>();
		return edges;
	}

}

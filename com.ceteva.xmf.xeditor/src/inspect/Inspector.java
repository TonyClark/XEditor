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

public class Inspector extends JInternalFrame  {

	private int objectHandle;
	private String objectLabel;

	public Inspector(String snapshot, int objectHandle, String objectLabel) {
		super(objectLabel, true, // resizable
				true, // closable
				true, // maximizable
				true);// iconifiable
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
	}

	public int getObjectHandle() {
		return objectHandle;
	}

	public String getObjectLabel() {
		return objectLabel;
	}

	public Vector<Edge> edges(Inspector target) {
		Vector<Edge> edges = new Vector<Edge>();
		return edges;
	}

}

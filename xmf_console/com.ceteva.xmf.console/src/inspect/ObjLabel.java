package inspect;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;

import inspect.value.InspectorElement;
import inspect.value.InspectorValue;

public class ObjLabel extends JLabel {

	private static int MAX_STRING = 20;

	private int objectHandle;
	private String objectLabel;

	public ObjLabel(int objectHandle, String objectLabel) {
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		setText(trim(objectLabel));
		setToolTipText(objectLabel);
		ToolTipManager.sharedInstance().registerComponent(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mEvt) {
				JComponent component = (JComponent) mEvt.getSource();
				TransferHandler tHandler = component.getTransferHandler();
				tHandler.exportAsDrag(component, mEvt, TransferHandler.COPY);
			}
		});
		setTransferHandler(new ObjLabelTransferHandler(this));
		setFont(InspectorTable.HEADER_FONT);
	}

	private String trim(String text) {
		if (text.length() <= MAX_STRING)
			return text;
		else
			return text.substring(0, MAX_STRING) + "...";
	}

	public int getObjectHandle() {
		return objectHandle;
	}

	public String getObjectLabel() {
		return objectLabel;
	}

	public InspectorValue getValue() {
		return new InspectorElement(objectHandle, objectLabel, new Vector<String>());
	}

	public void refresh(int objectHandle, String objectLabel) {
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		setText(trim(objectLabel));
		setToolTipText(objectLabel);
	}

}

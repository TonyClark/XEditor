package inspect;

import java.awt.MouseInfo;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import console.Console;

public class DesktopTransferHandler extends TransferHandler {

	public int getSourceActions(JComponent c) {
		return DnDConstants.ACTION_NONE;
	}

	public Transferable createTransferable(JComponent comp) {
		return null;
	}

	public boolean canImport(TransferHandler.TransferSupport info) {
		if (info.isDataFlavorSupported(TransferableInspectorValue.INSPECTOR_FLAVOR)) {
			return true;
		}
		if (info.isDataFlavorSupported(TransferableFieldName.INSPECTOR_FLAVOR)) {
			return true;
		}
		return false;
	}

	public boolean importData(TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		Object obj = null;
		try {
			obj = support.getTransferable().getTransferData(TransferableInspectorValue.INSPECTOR_FLAVOR);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		if (obj != null && obj instanceof TransferableInspectorValue) {
			try {
				// Moved the element into the void...
				String xml = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
				String snapshot = InspectorManager.MANAGER.currentSnapshotName();
				Console.call("editXML", new Object[] { xml,snapshot }, (i) -> {
					if (i != null && i instanceof Inspector) {
						int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
						int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
						Inspector inspector = (Inspector) i;
						inspector.setLocation(x, y - (inspector.getHeight() / 2));
					}
				});
				return true;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		try {
			obj = support.getTransferable().getTransferData(TransferableFieldName.INSPECTOR_FLAVOR);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		if (obj != null && obj instanceof TransferableFieldName) {
			try {
				TransferableFieldName t = (TransferableFieldName) obj;
				String snapshot = InspectorManager.MANAGER.currentSnapshotName();
				Console.call("dropFieldName", new Object[] { t.getHandle(), t.getName(), snapshot }, (i) -> {
					if (i != null && i instanceof Inspector) {
						int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
						int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
						Inspector inspector = (Inspector) i;
						inspector.setLocation(x, y - (inspector.getHeight() / 2));
					}
				});
				return true;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return false;
	}

}

package inspect;

import java.awt.MouseInfo;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import console.Console;

public class ObjLabelTransferHandler extends TransferHandler {

	private ObjLabel label;

	public ObjLabelTransferHandler(ObjLabel label) {
		this.label = label;
	}

	public int getSourceActions(JComponent c) {
		return DnDConstants.ACTION_COPY_OR_MOVE;
	}

	public Transferable createTransferable(JComponent comp) {
		return new TransferableInspectorValue(label.getValue());
	}

	@Override
	protected void exportDone(JComponent source, Transferable transferable, int action) {
		if (action == TransferHandler.NONE) {
			// Moved the element into the void...
			Console.CONSOLE.call(label.getObjectHandle(), "edit", new Object[] {}, (i) -> {
				int       x         = (int) MouseInfo.getPointerInfo().getLocation().getX();
				int       y         = (int) MouseInfo.getPointerInfo().getLocation().getY();
				Inspector inspector = (Inspector) i;
				inspector.setLocation(x, y - (inspector.getHeight() / 2));
			});
		} else
			super.exportDone(source, transferable, action);
	}

}

package inspect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import inspect.value.InspectorValue;

public class TransferableInspectorValue implements Transferable {

	public static final DataFlavor INSPECTOR_FLAVOR = new DataFlavor(InspectorValue.class, "InspectorValue");
	private InspectorValue value;

	public TransferableInspectorValue(InspectorValue value) {
		this.value = value;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { INSPECTOR_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == INSPECTOR_FLAVOR;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor == INSPECTOR_FLAVOR)
			return this;
		else
			return value.getXML();
	}

}

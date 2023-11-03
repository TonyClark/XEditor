package inspect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import inspect.value.InspectorValue;

public class TransferableFieldName implements Transferable {

	public static final DataFlavor INSPECTOR_FLAVOR = new DataFlavor(InspectorField.class, "FieldName");
	private int handle;
	private String name;

	public TransferableFieldName(int handle, String name) {
		this.handle = handle;
		this.name = name;
	}

	public int getHandle() {
		return handle;
	}

	public String getName() {
		return name;
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
		return this;
	}

}

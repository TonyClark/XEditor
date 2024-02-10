package inspect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import console.Console;
import inspect.value.InspectorList;
import inspect.value.InspectorValue;

public class InspectorTransferHandler extends TransferHandler {

	private static final Font FONT = new Font("Monaco", Font.PLAIN, 16);

	private InspectorTable table;

	public InspectorTransferHandler(InspectorTable table) {
		this.table = table;
	}

	public int getSourceActions(JComponent c) {
		return DnDConstants.ACTION_COPY_OR_MOVE;
	}

	public Transferable createTransferable(JComponent comp) {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		InspectorField field = table.getFields().get(row);
		if (col == 1) {
			InspectorValue value = field.getValue();
			if (value instanceof InspectorList) {
				InspectorList list = (InspectorList) value;
				value = list.getSelectedValue();
			}
			BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			g.setFont(FONT);
			String text = value.toString();
			int height = g.getFontMetrics().getHeight();
			int width = g.getFontMetrics().stringWidth(text);
			g.setColor(Color.white);
			g.drawRect(0, 0, width, height);
			g.setColor(Color.black);
			g.drawString(text, 0, height);
			setDragImage(image);
			return new TransferableInspectorValue(value);
		} else
			return new TransferableFieldName(table.getInspector().getObjectHandle(), field.getName());
	}

	public boolean canImport(TransferHandler.TransferSupport info) {
		if (!info.isDataFlavorSupported(TransferableInspectorValue.INSPECTOR_FLAVOR)) {
			return false;
		}
		return true;
	}

	public boolean importData(TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		Transferable transferable = support.getTransferable();
		Component target = support.getComponent();
		if (target instanceof InspectorTable) {
			InspectorTable table = (InspectorTable) target;
			Inspector inspector = table.getInspector();
			DropLocation dropLocation = support.getDropLocation();
			if (!canImport(support)) {
				return false;
			}
			JTable.DropLocation dl = (JTable.DropLocation) dropLocation;
			int row = dl.getRow();
			int col = dl.getColumn();
			String xml;
			if (col == 1) {
				try {
					xml = (String) transferable.getTransferData(DataFlavor.stringFlavor);
					InspectorField field = table.getFields().get(row);
					Console.call("provisionallySetSlot",
							new Object[] { inspector.getObjectHandle(), field.getName(), xml }, (values) -> {
								// The values are in the form [ok,value] where
								// ok states that the update can go ahead.
								// value is an inspector value
								Vector<Object> vs = (Vector<Object>) values;
								boolean ok = (boolean) vs.get(0);
								InspectorValue value = (InspectorValue) vs.get(1);
								if (ok) {
									field.setValue(value);
									table.setDroppedCellValue(value, row, col);
									table.repaint();
								}
							});

				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			} else
				return false;
		} else

		{
			return false;
		}
	}

}

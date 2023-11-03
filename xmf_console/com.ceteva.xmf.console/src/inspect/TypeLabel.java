package inspect;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import console.Console;

public class TypeLabel extends ObjLabel implements MouseListener {

	public TypeLabel(int objectHandle, String objectLabel) {
		super(objectHandle, objectLabel);
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown() || e.isMetaDown()) {
			instantiate();
		}
	}

	private void instantiate() {
		Console.CONSOLE.send("invokeThenEdit",new Object[] {getObjectHandle(),"new",new Vector<Object>()});
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isControlDown() || e.isMetaDown()) {
			instantiate();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

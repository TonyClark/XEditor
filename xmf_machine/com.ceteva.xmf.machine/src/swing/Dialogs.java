package swing;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Dialogs {

	public static String getString(String title, String message, String value) {
		String[] result = new String[] { null };
		try {
			SwingUtilities.invokeAndWait(() -> {
				result[0] = (String) JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE,
						null, null, value);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return result[0];
	}

	public static Object choose(String title, String message, Vector<Object> options) {
		Object response = JOptionPane.showInputDialog(null, title, message, JOptionPane.QUESTION_MESSAGE, null,
				options.toArray(), options.get(0));
		return response;
	}

}

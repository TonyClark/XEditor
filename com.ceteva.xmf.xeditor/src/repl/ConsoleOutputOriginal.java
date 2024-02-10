package repl;

import java.io.IOException;
import javax.swing.*;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class ConsoleOutputOriginal extends OutputStream {

	private ConsoleTextArea editor;

	public ConsoleOutputOriginal(ConsoleTextArea editor) {
		this.editor = editor;
	}

	@Override
	public void write(int b) throws IOException {
		writeChar((char) b);
	}

	private void writeChar(char b) {
		SwingUtilities.invokeLater(() -> {
			editor.append("" + b);
			editor.setCaretPosition(editor.getText().length());
		});
	}

}

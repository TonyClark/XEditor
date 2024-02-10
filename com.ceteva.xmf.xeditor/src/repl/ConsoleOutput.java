package repl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;

import javax.swing.SwingUtilities;

public class ConsoleOutput extends OutputStream {

	private ConsoleTextArea     editor;
	private ArrayDeque<Integer> queue = new ArrayDeque<Integer>();

	public ConsoleOutput(ConsoleTextArea editor) {
		this.editor = editor;
		startMonitor();
	}

	private void startMonitor() {
		new Thread(() -> {
			while (true) {
				int c = -1;
				synchronized (queue) {
					if (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						c = queue.remove();
					}
				}
				if (c != -1) {
					writeChar((char) c);
				}
			}
		}).start();
	}

	@Override
	public void write(int b) throws IOException {
		synchronized(queue) {
			queue.add(b);
			queue.notifyAll();
		}
	}

	private void writeChar(char b) {
		SwingUtilities.invokeLater(() -> {
			editor.append("" + b);
			editor.setCaretPosition(editor.getText().length());
		});
	}

}

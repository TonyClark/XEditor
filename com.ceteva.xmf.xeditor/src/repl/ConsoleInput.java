package repl;

import java.io.IOException;
import java.io.InputStream;

public class ConsoleInput extends InputStream {

	private String buffer = "";

	@Override
	public int read() throws IOException {
		synchronized (this) {
			if (buffer.length() ==0) {
				try {
					wait();
					return read();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return 0;
				}
			} else {
				char r = buffer.charAt(0);
				buffer = buffer.substring(1,buffer.length());
				return r;
			}
		}
	}

	public synchronized void add(char keyChar) {
		buffer += keyChar;
		notifyAll();
	}

}

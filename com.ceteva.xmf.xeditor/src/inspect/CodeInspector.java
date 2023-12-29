package inspect;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rtextarea.RTextScrollPane;

import console.Console;
import editor.EditorTextArea;
import editor.StatusBar;

public class CodeInspector extends Inspector {

	private static final int NEAR = 50;

	private int objectHandle;
	private String objectLabel;
	private EditorTextArea editor;
	private File file = null;

	public CodeInspector(String snapshot, int objectHandle, String objectLabel, String source) {
		super(snapshot, objectHandle, objectLabel);
		try {
			file = File.createTempFile("code_inspector", "xmf");
			FileUtils.writeStringToFile(file, source, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		StatusBar statusBar = new StatusBar();
		editor = new EditorTextArea(Console.CONSOLE, 100, 100, "text/XOCL", statusBar);
		editor.load(file);
		JScrollPane scroll = new RTextScrollPane(editor);
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		putClientProperty("JInternalFrame.frameType", "normal");
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		InspectorManager.MANAGER.setVisible(true);
		InspectorManager.MANAGER.addInspector(this, snapshot);
		int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
		int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
		setLocation(x, y - (getHeight() / 2));
		pack();
		setVisible(true);
	}

	public int getObjectHandle() {
		return objectHandle;
	}

	public String getObjectLabel() {
		return objectLabel;
	}

}

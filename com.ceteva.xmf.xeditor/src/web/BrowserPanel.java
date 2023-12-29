package web;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class BrowserPanel extends JFXPanel {

	private WebView webView;

	public BrowserPanel() {
		super();
		Platform.runLater(() -> {
			webView = new WebView();
			Scene scene = new Scene(webView);
			webView.getEngine().load("file://" + new File("doc/homepage.html").getAbsolutePath());
			webView.getEngine().locationProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov, final String oldLoc, final String loc) {
					// System.err.println("LOAD " + ov + " " + oldLoc + " " + loc);
					if (false) { // !loc.contains("bbc.co.uk")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								webView.getEngine().loadContent("<HTML> SPAM </HTML>");
							}
						});
					}
				}
			});
			setScene(scene);
		});
	}

	public void loadFile(String file) {
		if(webView != null) {
			webView.getEngine().load("file://" + new File(file).getAbsolutePath());
		}
	}

}

package web;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.io.File;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class BrowserPanel extends Panel {

	private WebView  webView;
	private JFXPanel jfxPanel = new JFXPanel();
	private URLPanel urlPanel = new URLPanel(this);

	public BrowserPanel(Consumer<BrowserPanel> init) { 
		super();
		setLayout(new BorderLayout());
		Platform.setImplicitExit(false);
		add(urlPanel, BorderLayout.SOUTH);
		add(jfxPanel, BorderLayout.CENTER);
		Platform.runLater(() -> {
			webView = new WebView();
			Scene scene = new Scene(webView);
			init.accept(this);
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
			jfxPanel.setScene(scene);
		});
	}

	public void loadFile(String file) {
		if (webView != null) {
			String location = "file://" + new File(file).getAbsolutePath();
			urlPanel.setLocation(location);
			Platform.runLater(() -> {
				try {
					webView.getEngine().load(location);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void setURL(String url) {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			webView.getEngine().load(url);
			if (!urlPanel.getLocation().equals(url)) {
				urlPanel.setLocation(url);
			}
		});
	}

}

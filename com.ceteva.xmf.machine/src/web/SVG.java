package web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVG {

	public static String plantUML2SVG(String diagram) {
		SourceStringReader reader = new SourceStringReader(diagram);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
			final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
			return svg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void browse(String path) {
		URI uri;
		try {
			uri = new URI("file:///" + path);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (URISyntaxException | IOException e1) {
			e1.printStackTrace();
		}
	}

}

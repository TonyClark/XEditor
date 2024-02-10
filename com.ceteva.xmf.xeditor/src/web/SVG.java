package web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SVG {

	public static String plantUML2SVG(String source) {
		SourceStringReader          reader = new SourceStringReader(source);
		final ByteArrayOutputStream os     = new ByteArrayOutputStream();
		try {
			reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
		return svg;
	}

	public static String plantUML2HTML(String source) {
		SourceStringReader          reader = new SourceStringReader(source);
		final ByteArrayOutputStream os     = new ByteArrayOutputStream();
		try {
			reader.outputImage(os, new FileFormatOption(FileFormat.HTML));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String html = new String(os.toByteArray(), Charset.forName("UTF-8"));
		return html;
	}

}

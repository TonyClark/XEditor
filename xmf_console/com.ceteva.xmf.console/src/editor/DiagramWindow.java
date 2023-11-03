package editor;

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.LinkActivationEvent;
import org.apache.batik.swing.svg.LinkActivationListener;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import console.Console;

public class DiagramWindow extends JFrame implements LinkActivationListener {

	private Hashtable<String, String> links; 
	private JSVGCanvas canvas;

	public DiagramWindow(String svg, Hashtable<String, String> links) {
		this.links = links;
		canvas = new JSVGCanvas(new DiagramUserAgent(), true, true);
		setSVG(svg);
		canvas.addLinkActivationListener(this);
		add(new JScrollPane(canvas), BorderLayout.CENTER);
		setSize(Console.CONSOLE_WIDTH, Console.CONSOLE_HEIGHT);
		setLocation(Console.CONSOLE_X, Console.CONSOLE_Y);
		setVisible(true);
	}

	private void setSVG(String svg) {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		SVGDocument document;
		try {
			document = factory.createSVGDocument("", new ByteArrayInputStream(svg.getBytes("UTF-8")));
			canvas.setSVGDocument(document);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void linkActivated(LinkActivationEvent e) {
		String link = e.getReferencedURI();
		for (String l : links.keySet()) {
			if (link.endsWith(l)) {
				setSVG(links.get(l));
			}
		}
	}

}

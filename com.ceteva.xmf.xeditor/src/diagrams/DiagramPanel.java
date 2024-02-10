package diagrams;

import java.awt.geom.AffineTransform;

import org.apache.batik.bridge.ViewBox;
import org.apache.batik.swing.svg.LinkActivationEvent;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

import console.Console;

public class DiagramPanel extends SVGPanel {

	private short  svgScale;
	private int    svgPadding;
	private String name;

	public DiagramPanel(String name, Console console) {
		super(console);
		this.name       = name;
		this.svgScale   = SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX;
		this.svgPadding = 5;
		setRecenterOnResize(true);
	}

	@Override
	public void linkClicked(LinkActivationEvent l) {
		String link = l.getReferencedURI();
		Console.CONSOLE.call("diagramLinkClicked", new Object[] { name, link }, (svg) -> {
			if (svg != null) {
				setSVG((String) svg);
			}
		});
	}

	@Override
	protected AffineTransform calculateViewingTransform(String svgElementIdentifier, SVGSVGElement svgElement) {
		SVGRect svgElementBounds = svgElement.getBBox();
		if (svgElementBounds != null) {
			float[] svgElementBoundsVector = new float[] { svgElementBounds.getX(), svgElementBounds.getY(), svgElementBounds.getWidth(), svgElementBounds.getHeight() };

			float svgEemenetScaleToHeight = getHeight() - svgPadding;
			float svgElementScaleToWidth = getWidth() - svgPadding;

			return ViewBox.getPreserveAspectRatioTransform(svgElementBoundsVector, svgScale, true, svgElementScaleToWidth, svgEemenetScaleToHeight);
		} else
			return super.calculateViewingTransform(svgElementIdentifier, svgElement);
	}

	public void setSvgScale(short svgScale) {
		this.svgScale = svgScale;
	}

	public void setSvgPadding(int svgPadding) {
		this.svgPadding = svgPadding;
	}

}

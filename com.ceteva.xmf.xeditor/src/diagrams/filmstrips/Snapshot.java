package diagrams.filmstrips;

import web.SVG;

public class Snapshot {

	private String plantUML;
	private String svg = null;

	public Snapshot(String plantUML) {
		super();
		this.plantUML = plantUML;
	}

	public String getSVG() {
		if (svg == null)
			svg = SVG.plantUML2SVG(plantUML);
		return svg;
	}
}

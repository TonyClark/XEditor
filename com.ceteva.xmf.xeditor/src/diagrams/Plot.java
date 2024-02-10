package diagrams;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;

public class Plot {

	private Hashtable<String, XYSeries> lines = new Hashtable<String, XYSeries>();
	private SVGGraphics2D               svg2d;
	private XYSeriesCollection          dataset;
	private int                         width;
	private int                         height;

	public Plot(String title, int width, int height) {
		svg2d       = new SVGGraphics2D(width, height);
		dataset     = new XYSeriesCollection();
		this.width  = width;
		this.height = height;
	}

	public void add(String lineLabel, double x, double y) {
		XYSeries line = getLine(lineLabel);
		line.add(x, y);
	}

	private XYSeries getLine(String lineLabel) {
		if (lines.containsKey(lineLabel))
			return lines.get(lineLabel);
		else {
			XYSeries line = new XYSeries(lineLabel);
			lines.put(lineLabel, line);
			return line;
		}
	}

	public String getSVG() {
		for (XYSeries line : lines.values()) {
			dataset.addSeries(line);
		}
		JFreeChart chart = ChartFactory.createXYLineChart("Allocation Performance", "Time", "Memory Usage", dataset);
		org.jfree.chart.plot.XYPlot plot = (XYPlot) chart.getPlot();

		XYItemRenderer renderer = plot.getRenderer();
		renderer.setStroke(new BasicStroke(5.0f));
		((AbstractRenderer) renderer).setAutoPopulateSeriesStroke(false);
		chart.draw(svg2d, new Rectangle2D.Double(0, 0, width, height));
		return svg2d.getSVGElement();
	}

}

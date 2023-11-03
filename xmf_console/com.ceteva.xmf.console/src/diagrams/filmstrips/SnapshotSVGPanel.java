package diagrams.filmstrips;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.apache.batik.swing.svg.LinkActivationEvent;

import console.Console;
import diagrams.SVGPanel;

public class SnapshotSVGPanel extends JPanel {

	private int       preferredWidth;
	private int       preferredHeight;
	private SVGPanel  svgPanel;
	private JTextArea label = new JTextArea();

	public SnapshotSVGPanel(Console console, String svg, String text, int width, int height) {
		setLayout(new BorderLayout());
		svgPanel = new SVGPanel(console) {

			@Override
			public void linkClicked(LinkActivationEvent l) {
			}
		};
		svgPanel.setSVG(svg);
		this.preferredWidth = width;
		this.preferredHeight = height;
		svgPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		add(svgPanel, BorderLayout.CENTER);
		add(label, BorderLayout.SOUTH);
		label.setLineWrap(true);
		label.setFont(new Font("Monospaced", Font.PLAIN, 8));
		label.setText(text);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(preferredWidth, preferredHeight);
	}

}

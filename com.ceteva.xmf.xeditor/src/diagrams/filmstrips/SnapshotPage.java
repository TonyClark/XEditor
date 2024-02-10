package diagrams.filmstrips;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class SnapshotPage extends JPanel {

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image         tmp  = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D    g2d  = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
	}

	private BufferedImage blankPage;

	static {
	}

	private JPanel imagePanel;
	private JPanel gridPanel = new JPanel();

	public SnapshotPage(int rows, int cols, int gap) {
		try {
			blankPage = ImageIO.read(new File("icons/blank_page.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		imagePanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				if (blankPage.getWidth() != getWidth() || blankPage.getHeight() != getHeight()) {
					blankPage = SnapshotPage.resize(blankPage, getWidth(), getHeight());
				}
				g.drawImage(blankPage, 0, 0, null);
			}
		};
		setLayout(new OverlayLayout(this));
		add(imagePanel);
		add(gridPanel);
		gridPanel.setLayout(new GridLayout(rows, cols, gap, gap));
	}

	public void addSnapshot(SnapshotSVGPanel panel) {
		gridPanel.add(panel);
	}

}

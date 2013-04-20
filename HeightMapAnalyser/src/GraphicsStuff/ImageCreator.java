package GraphicsStuff;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * 
 * @author Brandon James Talbot
 *
 * Static class to make a image for a given height map
 */

public class ImageCreator {
	/**
	 * This method creates a heat map for a map
	 * @param map the map to use
	 * @return The buffered image of the map
	 */
	public static BufferedImage createImage(Data.HeightMap map) {

		BufferedImage image = new BufferedImage(map.getData().length,
				map.getData()[0].length, BufferedImage.TYPE_INT_RGB);

		double distHeight = map.getMaxHeight() - map.getMinHeight();
		colour blue = new colour(0, 0, 255);
		colour green = new colour(0, 255, 0);
		colour red = new colour(255, 0, 0);
		colour orange = new colour(255, 140, 0);
		double b2g = blue.distanceTo(green);
		double g2o = green.distanceTo(orange);
		double o2r = orange.distanceTo(red);
		double b2r = b2g + o2r + g2o;

		double greenPnt = distHeight * (b2g / b2r);
		double orangePnt = distHeight * ((b2g + g2o) / b2r);

		for (int x = 0; x < map.getData().length; ++x) {
			for (int y = 0; y < map.getData()[x].length; ++y) {
				double h = map.getData()[x][y] - map.getMinHeight();

				colour out = new colour(0, 0, 0);

				// System.out.println(h + " " + midPnt);

				if (h < greenPnt) {
					out.r = (int) (blue.r + ((h / greenPnt) * (green.r - blue.r)));
					out.g = (int) (blue.g + ((h / greenPnt) * (green.g - blue.g)));
					out.b = (int) (blue.b + ((h / greenPnt) * (green.b - blue.b)));
				} else if (h < orangePnt) {
					h -= greenPnt;
					out.r = (int) (green.r + ((h / (orangePnt - greenPnt)) * (orange.r - green.r)));
					out.g = (int) (green.g + ((h / (orangePnt - greenPnt)) * (orange.g - green.g)));
					out.b = (int) (green.b + ((h / (orangePnt - greenPnt)) * (orange.b - green.b)));
				} else {
					h -= (orangePnt);

					out.r = (int) (orange.r + ((h / (distHeight - (orangePnt))) * (red.r - orange.r)));
					out.g = (int) (orange.g + ((h / (distHeight - (orangePnt))) * (red.g - orange.g)));
					out.b = (int) (orange.b + ((h / (distHeight - (orangePnt))) * (red.b - orange.b)));
					// out = red;
				}

				// System.out.println(out.r + " " + out.g + " " + out.b);

				image.setRGB(x, y, new Color(out.r, out.g, out.b).getRGB());
			}
		}

		return image;
	}

	/**
	 * This adds a line in black to a given image
	 * @param image the image to use
	 * @param points the points to add the line
	 * @return
	 */
	public static BufferedImage addLine(BufferedImage image,
			Vector<Point> points) {
		BufferedImage out = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				if (points.contains(new Point(x, y)))
					out.setRGB(x, y, Color.black.getRGB());
				else
					out.setRGB(x, y, image.getRGB(x, y));
			}
		}

		return out;
	}
}

/**
 * Class for colour storage and arithmatic
 *
 */
class colour {
	public int r, g, b;

	public colour(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public double distanceTo(colour other) {
		int xd = this.r - other.r;
		int yd = this.g - other.g;
		int zd = this.b - other.b;
		return Math.sqrt(xd * xd + yd * yd + zd * zd);
	}
}
package Data;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.util.Vector;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         This class holds the list of Data that was drawn on the height map
 *         currently being shown. this also holds the Buffered image with the
 *         line drawn on it for display purposes.
 * 
 */

public class ActiveMap {
	// globals
	public HeightMap map;
	public Vector<Point> listOfPoints;

	BufferedImage image;

	/**
	 * Contructor
	 * 
	 * @param map
	 *            The Map that this active map is currently working on
	 */
	public ActiveMap(HeightMap map) {
		this.map = map;
		listOfPoints = new Vector<Point>();

		resetImage();
	}

	/**
	 * Converts the mouse position on the page to the map co-ordinates
	 * 
	 * @param p
	 *            The mouse position
	 * @param width
	 *            the width of the drawing window
	 * @param height
	 *            the height of the drawing window
	 * @return The point in the image co-ordinates
	 */
	public Point convertToImageCoord(Point p, double width, double height) {
		Point out = new Point();
		out.x = (int) ((double) p.x * (map.getImage().getWidth() / width));
		out.y = (int) ((double) p.y * (map.getImage().getHeight() / height));

		// System.out.println(p.x + " : " + p.y + " ---- " + out.x + " : " +
		// out.y);

		return out;
	}

	/**
	 * Takes in all variables to convert, but also adds the point to the list
	 * 
	 * @param p
	 *            mouse point on drawing screen
	 * @param width
	 *            drawing screen width
	 * @param height
	 *            drawing screen height
	 * @param points
	 *            points list to add to
	 */
	public void convertToImageCoordAndAdd(Point p, double width, double height,
			Vector<Point> points) {
		Point newPnt = convertToImageCoord(p, width, height);
		if (newPnt.x >= 0 && newPnt.x < map.getImage().getWidth()
				&& newPnt.y >= 0 && newPnt.y < map.getImage().getHeight()) {
			if (points.size() > 0) {
				Point old = new Point(points.lastElement().x,
						points.lastElement().y);

				while (!old.equals(newPnt)) {
					old.x = old.x > newPnt.x ? old.x - 1
							: old.x < newPnt.x ? old.x + 1 : old.x;
					old.y = old.y > newPnt.y ? old.y - 1
							: old.y < newPnt.y ? old.y + 1 : old.y;
					image.setRGB(old.x, old.y, Color.black.getRGB());
					points.add(new Point(old.x, old.y));
				}
			}

			image.setRGB(newPnt.x, newPnt.y, Color.black.getRGB());
			if (!points.contains(newPnt))
				points.add(newPnt);
		}
	}

	/**
	 * Resets the image to the origional state without a line
	 */
	public void resetImage() {
		image = new BufferedImage(map.getImage().getWidth(), map.getImage()
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < map.getImage().getWidth(); ++x)
			for (int y = 0; y < map.getImage().getHeight(); ++y)
				image.setRGB(x, y, map.getImage().getRGB(x, y));
	}

	/**
	 * resets the image to have a line drawn on it
	 */
	public void resetImageWithLine() {
		image = new BufferedImage(map.getImage().getWidth(), map.getImage()
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < map.getImage().getWidth(); ++x)
			for (int y = 0; y < map.getImage().getHeight(); ++y)
				if (listOfPoints.contains(new Point(x, y)))
					image.setRGB(x, y, Color.black.getRGB());
				else
					image.setRGB(x, y, map.getImage().getRGB(x, y));
	}

	/**
	 * Sets the list to the given input
	 * 
	 * @param points
	 *            List of points to set to
	 */
	public void setList(Vector<Point> points) {
		this.listOfPoints = points;
		// resetImage();
	}

	/**
	 * Gets the source for the image
	 * 
	 * @return the images source
	 */
	public ImageProducer getImage() {
		return image.getSource();
	}

	/**
	 * setter for the map
	 * 
	 * @param map
	 *            the map to set to
	 */
	public void setMap(Data.HeightMap map) {
		this.map = map;
		resetImageWithLine();
	}
}

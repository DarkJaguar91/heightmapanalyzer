package Data;

import java.awt.image.BufferedImage;

/**
 * 
 * @author Brandon James Talbot
 *
 * This contains all the data a height map can hold.
 * This holds the data in an array.
 * The x min and max
 * The y min and max
 * the height min and max
 * the step between each point
 * and a buffered image height map as temperature map
 */

public class HeightMap {
	// globals
	String name, path;
	double[][] data;
	double maxHeight, minHeight;
	double minX, maxX, minY, maxY, step;
	BufferedImage image;

	/**
	 * Constructor
	 * @param name The name of the height map
	 * @param data the array of heights
	 * @param maxHeight the max height
	 * @param minHeight the min height
	 * @param minX the min x pos
	 * @param maxX the max x pos
	 * @param minY then min y pos
	 * @param maxY the max y pos
	 * @param step the step between two points
	 */
	public HeightMap(String name, double[][] data, double maxHeight,
			double minHeight, double minX, double maxX, double minY,
			double maxY, double step) {
		super();
		this.name = name;
		this.path = name;
		this.data = data;
		this.maxHeight = maxHeight;
		this.minHeight = minHeight;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.step = step;
		image = GraphicsStuff.ImageCreator.createImage(this);
	}

	// getters and setters
	public String getPath(){
		return this.path;
	}
	public double[][] getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BufferedImage getImage() {
		return image;
	}

	public double getMaxHeight() {
		return maxHeight;
	}

	public double getMinHeight() {
		return minHeight;
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getStep() {
		return step;
	}
}

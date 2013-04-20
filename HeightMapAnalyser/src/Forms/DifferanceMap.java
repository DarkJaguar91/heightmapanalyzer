package Forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 
 * @author Brandon James Talbot
 *  
 *  Differance map
 *  Contains the method to create and display the differance map
 *
 */

public class DifferanceMap extends JPanel implements MouseListener,
		MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// globals
	public BufferedImage img;

	protected Point mousePoint;
	protected int width, height;
	protected float data[][];
	protected Data.HeightMap map1, map2;

	/**
	 * Constructor
	 * @param map1 first map
	 * @param map2 second map
	 */
	public DifferanceMap(Data.HeightMap map1, Data.HeightMap map2) {
		this.setPreferredSize(new Dimension(800, 600));

		mousePoint = new Point();

		img = makeImage(map1, map2);

		this.map1 = map1;
		this.map2 = map2;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		repaint();
	}

	/**
	 * Overide of paint method
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) (g);

		if (img != null)
			g2.drawImage(
					Toolkit.getDefaultToolkit().createImage(img.getSource()),
					0, 0, this.getWidth(), this.getHeight(), this);
		if (data != null) {
			g2.setColor(Color.RED);
			g2.drawString(mousePoint.x + " - " + mousePoint.y, 10, 10);
			g2.drawString("Differance: " + data[mousePoint.x][mousePoint.y],
					10, 25);
			g2.drawString("X point: " + mousePoint.x * map1.getStep(), 10, 40);
			g2.drawString("Y point: " + mousePoint.y * map1.getStep(), 10, 55);
		}
	}

	/**
	 * Creates The buffered image
	 * @param map1 first map
	 * @param map2 second map
	 * @return
	 */
	private BufferedImage makeImage(Data.HeightMap map1, Data.HeightMap map2) {
		width = map1.getData().length > map2.getData().length ? map2.getData().length
				: map1.getData().length;
		height = map1.getData()[0].length > map2.getData()[0].length ? map2
				.getData()[0].length : map1.getData()[0].length;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		data = new float[width][height];

		float minHeight = Math.max(
				Math.abs((float) map1.getMinHeight()
						- (float) map2.getMinHeight()),
				Math.abs((float) map1.getMaxHeight()
						- (float) map2.getMaxHeight()));
		float maxHeight = Math.max(
				Math.abs((float) map1.getMinHeight()
						- (float) map2.getMaxHeight()),
				Math.abs((float) map1.getMaxHeight()
						- (float) map2.getMinHeight()))
				- minHeight;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				float amount = Math.abs((float) map1.getData()[x][y]
						- (float) map2.getData()[x][y]);
				data[x][y] = amount;
				float value = (Math.abs(amount - minHeight) / maxHeight) * 255;
				Color col = new Color((int) value, (int) value, (int) value);
				image.setRGB(x, y, col.getRGB());
			}
		}

		return image;
	}

	/**
	 * Getters and setters
	 * Mouse methods 2
	 */
	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public synchronized void mouseMoved(MouseEvent arg0) {
		// mousePoint.x = (int)(((float)arg0.getX() / (float)this.getWidth()) *
		// (float)width);
		// mousePoint.y = (int)(((float)arg0.getY() / (float)this.getHeight()) *
		// (float)height);
		mousePoint = new Point(
				(int) (((float) arg0.getX() / (float) this.getWidth()) * (float) width),
				(int) (((float) arg0.getY() / (float) this.getHeight()) * (float) height));
		repaint();
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseWheelMoved(MouseEvent arg0) {
	}
}
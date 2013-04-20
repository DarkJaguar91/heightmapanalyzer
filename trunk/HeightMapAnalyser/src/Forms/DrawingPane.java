package Forms;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;

import javax.swing.JLabel;

import Data.ActiveMap;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         Pane to draw on This alows for the drawing of the line on a height
 *         map Displaying of the line on the height map and the graph creation
 */

public class DrawingPane extends JLabel implements MouseListener,
		MouseMotionListener, MouseWheelListener {

	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	ActiveMap hMap = null;
	Vector<Point> tempPoints;
	MainForm mForm;
	ImageDrawer parent;

	private int drawingType = 0;

	/**
	 * Constructor
	 * 
	 * @param mForm
	 *            The main form
	 * @param parent
	 *            , the parent form with label to show the cursor position
	 */
	public DrawingPane(MainForm mForm, ImageDrawer parent) {
		super("Image");
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		this.parent = parent;
		this.mForm = mForm;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}

	/**
	 * Override of the paint method
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) (g);

		if (hMap != null)
			g2.drawImage(
					Toolkit.getDefaultToolkit().createImage(hMap.getImage()),
					0, 0, this.getWidth(), this.getHeight(), this);
	}

	/**
	 * Sets the image to display
	 * 
	 * @param map
	 *            height map to display
	 */
	public void setImage(Data.ActiveMap map) {
		this.hMap = map;
		repaint();
	}

	/**
	 * adds a point to the line thats being drawn
	 * 
	 * @param p
	 *            the mouse point
	 */
	private void addPoint(Point p) {
		if (hMap != null) {

			if (drawingType == 0) {
				hMap.convertToImageCoordAndAdd(p, this.getWidth(),
						this.getHeight(), tempPoints);
			} else if (drawingType == 1) {
				tempPoints = new Vector<Point>();
				hMap.resetImage();

				int yPos = p.y;

				for (int i = 0; i <= this.getWidth(); i++) {
					hMap.convertToImageCoordAndAdd(new Point(i, yPos),
							this.getWidth(), this.getHeight(), tempPoints);
				}
			} else if (drawingType == 2) {
				tempPoints = new Vector<Point>();
				hMap.resetImage();

				int xPos = p.x;

				for (int i = 0; i <= this.getWidth(); i++) {
					hMap.convertToImageCoordAndAdd(new Point(xPos, i),
							this.getWidth(), this.getHeight(), tempPoints);
				}
			} else if (drawingType == 3) {
				tempPoints = new Vector<Point>();
				hMap.resetImage();

				int minAmount = Math.min(p.x, this.getHeight() - p.y);

				Point start = new Point(p.x - minAmount, p.y + minAmount);

				while (start.x < this.getWidth() && start.y > 0) {
					hMap.convertToImageCoordAndAdd(start, this.getWidth(),
							this.getHeight(), tempPoints);

					start = new Point(start.x + 1, start.y - 1);
				}
			} else if (drawingType == 4) {
				tempPoints = new Vector<Point>();
				hMap.resetImage();

				int minAmount = Math.min(p.x, p.y);

				Point start = new Point(p.x - minAmount, p.y - minAmount);

				while (start.x < this.getWidth() && start.y < this.getHeight()) {
					hMap.convertToImageCoordAndAdd(start, this.getWidth(),
							this.getHeight(), tempPoints);

					start = new Point(start.x + 1, start.y + 1);
				}
			}

			if (tempPoints.size() > 1) {
				hMap.setList(tempPoints);
				mForm.drawGraph();
				repaint();
			}
		}
	}

	/**
	 * changes the drawing type
	 * 
	 * @param drawingType
	 *            The type to draw with
	 */
	public void setDrawingType(int drawingType) {
		this.drawingType = drawingType;
	}

	/**
	 * Mouse listener required Getters and setters
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		addPoint(e.getPoint());
		if (hMap != null) {
			Point pnt = hMap.convertToImageCoord(e.getPoint(), this.getWidth(),
					this.getHeight());
			parent.setMousePoint(pnt.x, pnt.y, hMap);
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (hMap != null) {
			Point pnt = hMap.convertToImageCoord(e.getPoint(), this.getWidth(),
					this.getHeight());
			parent.setMousePoint(pnt.x, pnt.y, hMap);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (hMap != null) {
			tempPoints = new Vector<Point>();
			hMap.resetImage();
		}
		addPoint(e.getPoint());
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public Data.ActiveMap gethMap() {
		return hMap;
	}
}

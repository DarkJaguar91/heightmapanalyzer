package Forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * @author Brandon James Talbot
 *
 * This form Contains the Graph containing the heights of each map along the designated line
 */

public class GraphContainer extends JInternalFrame {

	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	ChartPanel panel = null;

	/**
	 * Constructor (intialises the jinternal frame components)
	 */
	public GraphContainer(Dimension formSize) {
		super("Graph");

		setLayout(new BorderLayout());

		// setResizable(true);

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		panel = new ChartPanel(null);

		Dimension screenSize = formSize;

		panel.setPreferredSize(new Dimension((int) (screenSize.width * 0.5f),
				(int) (screenSize.height * 0.7f)));

		this.add(panel, BorderLayout.CENTER);

		createChart(null, new Vector<Data.HeightMap>());

		setVisible(true);
	}

	/**
	 * Gets the graph as a buffered image
	 * @return The graph as a buffered image
	 */
	public BufferedImage getGraph() {
		BufferedImage img = new BufferedImage(panel.getWidth(),
				panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();

		panel.getChart().draw(
				g2,
				new Rectangle2D.Double(0, 0, panel.getWidth(), panel
						.getHeight()));

		g2.dispose();
		return img;
	}

	/**
	 * Creates the chart given the data
	 * @param map The active map (for the line)
	 * @param maps The list of maps to draw
	 */
	public void createChart(Data.ActiveMap map, Vector<Data.HeightMap> maps) {

		if (map == null)
		   return;
		XYSeriesCollection set = new XYSeriesCollection();

		// System.out.println(maps.size());

		int sizeBeforeCenterx = Math.abs((int)(map.map.getMinX() / map.map.getStep()));
		int sizeBeforeCentery = Math.abs((int)(map.map.getMinY() / map.map.getStep()));
		for (Data.HeightMap hmap : maps) {
			XYSeries series = new XYSeries(hmap.getName());

			int currentsbcx = Math.abs((int)(hmap.getMinX() / hmap.getStep()));
			int currentsbcy = Math.abs((int)(hmap.getMinY() / hmap.getStep()));
			int diffx = sizeBeforeCenterx - currentsbcx;
			int diffy = sizeBeforeCentery - currentsbcy;

			for (int i = 0; i < map.listOfPoints.size(); ++i) {
				if (map.listOfPoints.elementAt(i).x + diffx < hmap.getImage()
						.getWidth()
						&& map.listOfPoints.elementAt(i).y + diffy < hmap.getImage()
						.getHeight() && map.listOfPoints.elementAt(i).x + diffx >= 0 && map.listOfPoints.elementAt(i).y + diffy >= 0)
					series.add(i, hmap.getData()[map.listOfPoints
					                             .elementAt(i).x + diffx][map.listOfPoints.elementAt(i).y + diffy]);
			}

			set.addSeries(series);
		}

		final JFreeChart chart = ChartFactory.createXYLineChart("Height Map", // chart
																				// title
				"X-Distance", // x axis label
				"Heigth", // y axis label
				set, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);

		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		panel.setChart(chart);

		repaint();
	}
}

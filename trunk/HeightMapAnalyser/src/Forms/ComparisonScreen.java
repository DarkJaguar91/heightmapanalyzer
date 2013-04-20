package Forms;

import gov.noaa.pmel.sgt.ColorMap;
import gov.noaa.pmel.sgt.ContourLevels;
import gov.noaa.pmel.sgt.GridAttribute;
import gov.noaa.pmel.sgt.IndexedColorMap;
import gov.noaa.pmel.sgt.LinearTransform;
import gov.noaa.pmel.sgt.SGLabel;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Dimension2D;
import gov.noaa.pmel.util.Point2D;
import gov.noaa.pmel.util.Range2D;
import gov.noaa.pmel.util.Rectangle2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Drawing3D.HeightMap3DRenderer;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         The comparison Screen This holds many tabbed pages in order to
 *         compare 2 height maps visually.
 * 
 *         Contains a differance map Contour maps 3D map comparison text
 *         comparison RMSD and the graph containing only the 2 selected maps (if
 *         a line was drawn already)
 * 
 */

public class ComparisonScreen extends JPanel {
	/**
	 * globals
	 */
	private static final long serialVersionUID = 1L;
	JTabbedPane pane;
	JPlotLayout jpl1, jpl2;
	Drawing3D.HeightMap3DRenderer threeDRenderer;
	Vector<Data.HeightMap> maps;
	DifferanceMap diff = null;
	boolean hasGraph = false;
	Data.ActiveMap savedActiveMap = null;

	ChartPanel chrt;

	/**
	 * Makes the differance map as a thread
	 */
	private Runnable makeDiffMap = new Runnable() {

		public void run() {
			diff = new DifferanceMap(maps.elementAt(0), maps.elementAt(1));

			pane.addTab("Differance Map", diff);
		}
	};

	/**
	 * makes the contour map as a thread
	 */
	private Runnable makeContours = new Runnable() {

		public void run() {
			jpl1 = makeGraph(maps.elementAt(0));
			jpl2 = makeGraph(maps.elementAt(1));

			JPanel contourMapHolder = new JPanel(new GridLayout());

			JPanel plot1 = new JPanel();
			JPanel plot2 = new JPanel();
			plot1.setLayout(new BorderLayout());
			plot2.setLayout(new BorderLayout());
			jpl1.setPreferredSize(plot1.getSize());
			jpl2.setPreferredSize(plot2.getSize());
			plot1.add(jpl1, BorderLayout.CENTER);
			plot2.add(jpl2, BorderLayout.CENTER);

			contourMapHolder.add(plot1);
			contourMapHolder.add(plot2);
			pane.addTab("Contour Maps", contourMapHolder);
		}
	};

	/**
	 * creates the text comparison and RMSD as a thread
	 */
	private Runnable makeCompareScreen = new Runnable() {

		public void run() {
			JPanel p = new JPanel();

			p.setLayout(new GridLayout(0, 2));

			JPanel map1 = makeComparison(maps.elementAt(0));
			JPanel map2 = makeComparison(maps.elementAt(1));

			p.add(map1);
			p.add(map2);

			p.add(new JLabel("RMSD: " + RMSD()));
			pane.addTab("Comparison", p);
		}
	};

	/**
	 * Creates the graph given a certain active map
	 * 
	 * @param map
	 *            The active map containing the list
	 */
	public void createChart(Data.ActiveMap map) {
		hasGraph = true;
		savedActiveMap = map;
		JPanel pnl = new JPanel();

		chrt = new ChartPanel(null);

		XYSeriesCollection set = new XYSeriesCollection();

		// System.out.println(maps.size());

		for (Data.HeightMap hmap : maps) {
			XYSeries series = new XYSeries(hmap.getName());

			for (int i = 0; i < map.listOfPoints.size(); ++i) {
				if (map.listOfPoints.elementAt(i).x < hmap.getImage()
						.getWidth()
						&& map.listOfPoints.elementAt(i).y < hmap.getImage()
								.getHeight())
					series.add(
							i,
							hmap.getData()[map.listOfPoints.elementAt(i).x][map.listOfPoints
									.elementAt(i).y]);
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

		chrt.setChart(chart);

		pnl.setLayout(new BorderLayout());

		pnl.add(chrt, BorderLayout.CENTER);

		pane.addTab("Graph", pnl);
	}

	/**
	 * creates the comparison panel for a certain map
	 * 
	 * @param map
	 *            the map to create the screen for
	 * @return the jpanel containing the comparison values
	 */
	private JPanel makeComparison(Data.HeightMap map) {
		JPanel panel = new JPanel();
		JLabel img = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(map.getImage().getSource())));
		JLabel x = new JLabel("X values: " + map.getMinX() + " to "
				+ map.getMaxX());
		JLabel y = new JLabel("Y values: " + map.getMinY() + " to "
				+ map.getMaxY());
		JLabel Height = new JLabel("Height values: " + map.getMinHeight()
				+ " to " + map.getMaxHeight());

		panel.setLayout(new GridLayout(0, 1));

		img.setBorder(BorderFactory.createBevelBorder(1));
		panel.setBorder(BorderFactory.createBevelBorder(1));

		panel.add(img);
		panel.add(x);
		panel.add(y);
		panel.add(Height);

		return panel;
	}

	final static int NUM_CONTOUR_LINES = 10;

	/**
	 * Constructor
	 */
	public ComparisonScreen() {
		// super("Comparison");

		setSize(850, 650);

		setMinimumSize(new Dimension(850, 650));

		this.setLayout(new BorderLayout());

		pane = new JTabbedPane();

		this.add(pane, BorderLayout.CENTER);

		setVisible(true);
	}

	/**
	 * Outputs this to CSV
	 */
	public void CSV() {
		try {
			JFileChooser chooser = new JFileChooser();

			int fd = chooser.showSaveDialog(this);

			if (fd == JFileChooser.APPROVE_OPTION) {
				boolean success = (new File(chooser.getSelectedFile()
						.getAbsolutePath())).mkdir();

				if (success) {
					String path = chooser.getSelectedFile().getAbsolutePath();

					printMaps(path);

					printContours(path);

					// differance map
					ImageIO.write(diff.img, "jpg", new File(path
							+ "/DifferanceMap.jpg"));
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									"Im sorry, but you do not have the apropriate permissions to save here.\n"
											+ "Please contact your administrator to rectify this.",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints out the contour maps
	 * 
	 * @param path
	 *            The path to output to
	 */
	private void printContours(String path) throws IOException {
		BufferedImage img = new BufferedImage(jpl1.getWidth(),
				jpl1.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		jpl1.draw(g2, jpl1.getWidth(), jpl1.getHeight());

		ImageIO.write(img, "jpg", new File(path + "/"
				+ maps.elementAt(0).getName() + "-ContourMap.jpg"));

		img = new BufferedImage(jpl2.getWidth(), jpl2.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		g2 = img.createGraphics();
		jpl2.draw(g2, jpl2.getWidth(), jpl2.getHeight());

		ImageIO.write(img, "jpg", new File(path + "/"
				+ maps.elementAt(1).getName() + "-ContourMap.jpg"));
	}

	/**
	 * Creates the CSV, heatmaps and graph
	 * 
	 * @param path
	 *            The path to output to
	 * @throws IOException
	 *             if it cant output to target space
	 */
	private void printMaps(String path) throws IOException {
		String CSVoutput = "";
		boolean first = true;

		// create heat maps and CSV
		for (Data.HeightMap map : maps) {
			if (!first)
				CSVoutput += "\n";

			ImageIO.write(map.getImage(), "jpg",
					new File(path + "/" + map.getName() + "-heatmap.jpg"));

			CSVoutput += map.getName() + "\n";

			CSVoutput += "X-Values: " + map.getMinX() + " to " + map.getMaxX()
					+ "\n";
			CSVoutput += "Y-Values: " + map.getMinY() + " to " + map.getMaxY()
					+ "\n";
			CSVoutput += "Height-Values: " + map.getMinHeight() + " to "
					+ map.getMaxHeight() + "\n";

			if (hasGraph) {
				CSVoutput += "LinePos\tImagePos\tHeight\n";
				Vector<Point> pnts = savedActiveMap.listOfPoints;
				for (int i = 0; i < pnts.size(); ++i) {
					CSVoutput += i
							+ "\t("
							+ (map.getMinX() + pnts.elementAt(i).x
									* map.getStep())
							+ ","
							+ (map.getMinY() + pnts.elementAt(i).y
									* map.getStep())
							+ ")\t"
							+ map.getData()[pnts.elementAt(i).x][pnts
									.elementAt(i).y] + "\n";
				}
			}

			first = false;
		}

		// Graph
		if (hasGraph) {
			BufferedImage img = new BufferedImage(chrt.getWidth(),
					chrt.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = img.createGraphics();
			chrt.getChart().draw(
					g2,
					new java.awt.geom.Rectangle2D.Double(0, 0, chrt.getWidth(),
							chrt.getHeight()));
			g2.dispose();
			ImageIO.write(img, "jpg", new File(path + "/Graph.jpg"));
		}
		CSVoutput += "\nRMSD\t" + RMSD() + "\n";

		FileWriter writer = new FileWriter(path + "/GraphCSV.csv");
		writer.write(CSVoutput);
		writer.close();
	}

	/**
	 * sets the data in the comparison screen
	 * 
	 * @param maps
	 */
	public void setData(Vector<Data.HeightMap> maps) {
		// pane = new JTabbedPane();
		pane.removeAll();

		hasGraph = false;

		this.maps = maps;

		Thread T1 = new Thread(makeDiffMap);
		Thread T2 = new Thread(makeContours);
		Thread T3 = new Thread(makeCompareScreen);
		T1.run();
		T2.start();
		T3.start();

		// this.add(pane, BorderLayout.CENTER);
	}

	/**
	 * Creates the 3D map (done not at start as its memory intensive and may not
	 * be wanted)
	 */
	public void create3DMap() {
		threeDRenderer = new HeightMap3DRenderer();
		threeDRenderer.setPreferredSize(new Dimension(800, 600));

		threeDRenderer.addHeightmap(maps.elementAt(0));
		threeDRenderer.addHeightmap(maps.elementAt(1));
		pane.addTab("3D Render", threeDRenderer);
	}

	/**
	 * Calculates the RMSD for the given maps
	 * 
	 * @return
	 */
	private float RMSD() {
		Data.HeightMap map1 = maps.elementAt(0);
		Data.HeightMap map2 = maps.elementAt(1);

		int mapx = Math.min(map1.getData().length, map2.getData().length);
		int mapy = Math.min(map1.getData()[0].length, map2.getData()[0].length);

		float sum = 0;

		for (int x = 0; x < mapx; ++x) {
			for (int y = 0; y < mapy; ++y) {
				sum = (float) (Math.pow((map1.getMinX() * map1.getStep() * x)
						- (map2.getMinX() * map2.getStep() * x), 2)
						+ Math.pow((map1.getMinY() * map1.getStep() * y)
								- (map2.getMinY() * map2.getStep() * y), 2) + Math
						.pow((map1.getData()[x][y]) - (map2.getData()[x][y]), 2));
			}
		}

		sum = (float) Math.sqrt(sum / 2);

		return sum;
	}

	/**
	 * creates a Contour map for the given height map
	 * 
	 * @param h
	 *            Height map
	 * @return Contour container
	 */
	JPlotLayout makeGraph(Data.HeightMap h) {
		/*
		 * This example uses a pre-created "Layout" for raster time series to
		 * simplify the construction of a plot. The JPlotLayout can plot a
		 * single grid with a ColorKey, time series with a LineKey, point
		 * collection with a PointCollectionKey, and general X-Y plots with a
		 * LineKey. JPlotLayout supports zooming, object selection, and object
		 * editing.
		 */
		SimpleGrid newData;
		JPlotLayout rpl;
		ContourLevels clevels;
		/*
		 * Create a test grid with sinasoidal-ramp data.
		 */

		double[] xData = new double[(int) ((h.getMaxX() - h.getMinX()) / h
				.getStep())];
		double[] yData = new double[(int) ((h.getMaxY() - h.getMinY()) / h
				.getStep())];
		double[] zData = new double[yData.length * xData.length];

		for (int i = 0; i < (int) ((h.getMaxX() - h.getMinX()) / h.getStep()); ++i)
			xData[i] = h.getMinX() + i * h.getStep();
		for (int i = 0; i < (int) ((h.getMaxX() - h.getMinX()) / h.getStep()); ++i)
			yData[i] = h.getMaxY() - i * h.getStep();
		for (int i = 0; i < yData.length; ++i)
			for (int j = 0; j < xData.length; ++j)
				zData[j + i * (xData.length)] = h.getData()[i][j];
		newData = new SimpleGrid(zData, xData, yData, "");
		/*
		 * Create the layout without a Logo image and with the ColorKey on a
		 * separate Pane object.
		 */
		rpl = new JPlotLayout(true, false, false, "", null, false);
		rpl.setEditClasses(false);
		/*
		 * Create a GridAttribute for CONTOUR style.
		 */
		Range2D datar = new Range2D(h.getMinHeight(), h.getMaxHeight(),
				(h.getMaxHeight() - h.getMinHeight()) / NUM_CONTOUR_LINES);
		clevels = ContourLevels.getDefault(datar);
		GridAttribute gridAttr_ = new GridAttribute(clevels);
		/*
		 * Create a ColorMap and change the style to RASTER_CONTOUR.
		 */
		gridAttr_.setColorMap(createColorMap(datar));
		gridAttr_.setStyle(GridAttribute.RASTER_CONTOUR);
		/*
		 * Add the grid to the layout and give a label for the ColorKey.
		 */
		rpl.setKeyLayerSizeP(new Dimension2D(0, 0));
		rpl.setKeyBoundsP(new Rectangle2D.Double(0.0, 0, 0.0, 0));
		newData.setKeyTitle(new SGLabel("KEY LABEL", "", new Point2D.Double(
				0.0, 0.0)));
		newData.setXMetaData(new SGTMetaData("x", "Position"));
		newData.setYMetaData(new SGTMetaData("Y", "Position"));
		rpl.addData(newData, gridAttr_, "");
		/*
		 * Change the layout's three title lines.
		 */
		rpl.setTitles("Contour Plot", "for", h.getName());
		/*
		 * Resize the graph and place in the "Center" of the frame.
		 */
		rpl.setSize(new Dimension(600, 400));
		/*
		 * Resize the key Pane, both the device size and the physical size. Set
		 * the size of the key in physical units and place the key pane at the
		 * "South" of the frame.
		 */
		// rpl.setKeyLayerSizeP(new Dimension2D(6.0, 1.02));
		// rpl.setKeyBoundsP(new Rectangle2D.Double(0.01, 1.01, 5.98, 1.0));

		return rpl;
	}

	/**
	 * generates a colour map for the contour map to use
	 * 
	 * @param datar
	 *            Range of the map
	 * @return the colour map
	 */
	private ColorMap createColorMap(Range2D datar) {
		int[] red = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 7, 23, 39, 55, 71, 87, 103, 119, 135, 151,
				167, 183, 199, 215, 231, 247, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 246, 228,
				211, 193, 175, 158, 140 };
		int[] green = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 27, 43, 59, 75, 91, 107,
				123, 139, 155, 171, 187, 203, 219, 235, 251, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103, 87, 71,
				55, 39, 23, 7, 0, 0, 0, 0, 0, 0, 0 };
		int[] blue = { 0, 143, 159, 175, 191, 207, 223, 239, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103, 87,
				71, 55, 39, 23, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0 };

		IndexedColorMap cmap = new IndexedColorMap(red, green, blue);
		cmap.setTransform(new LinearTransform(0.0, (double) red.length,
				datar.start, datar.end));
		return cmap;
	}
}

package Forms;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Data.ActiveMap;
import Data.HeightMap;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         Main form: This contains all the methods to do mouse checks and
 *         JMenuBar selections Also holds all frames as globals
 */

public class MainForm extends JFrame implements ActionListener {
	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	private ImageDrawer imageForm;
	private GraphContainer graphForm;
	private HeightMapList mapList;
	private JMenuItem loadImage, drawGraph, mexit, cexit, swap,
			CompareHeightMaps, mainCSV, comparCSV, mainScreen, Generate3D,
			about;
	private JMenu View, mainFile, comparisonFile;
	private JMenuBar bar;
	private JPanel rightContainer;

	private boolean graphInRight = true;

	private ComparisonScreen compScreen;

	private JFileChooser chooser;

	private Data.ActiveMap currentActiveMap = null;

	/**
	 * Constructor
	 */
	public MainForm() {
		super("Height Map Analyser");

		this.setIconImage((new ImageIcon("Images/icon.jpg")).getImage());

		setSize(800, 800);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		d.width = (int) (d.width * 0.5f);
		d.height = (int) (d.height * 0.65f);
		this.setMinimumSize(d);
		this.setPreferredSize(d);

		this.setExtendedState(MAXIMIZED_BOTH);

		this.setLayout(new BorderLayout());

		// menu bar

		bar = new JMenuBar();
		mainFile = new JMenu("File");
		comparisonFile = new JMenu("File");
		View = new JMenu("View");
		JMenu MExport = new JMenu("Export");
		JMenu CExport = new JMenu("Export");
		loadImage = new JMenuItem("Load Image");
		drawGraph = new JMenuItem("Draw Graph");
		mexit = new JMenuItem("Exit");
		cexit = new JMenuItem("Exit");
		CompareHeightMaps = new JMenuItem("Compare Maps");
		Generate3D = new JMenuItem("Show 3D Map");
		about = new JMenuItem("About");

		bar.setLayout(new FlowLayout(FlowLayout.LEFT));

		mainScreen = new JMenuItem("Graph Screen");
		mainCSV = new JMenuItem("CSV");
		comparCSV = new JMenuItem("CSV");

		swap = new JMenuItem("Swap Map and Graph");

		loadImage.addActionListener(this);
		drawGraph.addActionListener(this);
		mexit.addActionListener(this);
		cexit.addActionListener(this);
		swap.addActionListener(this);
		mainCSV.addActionListener(this);
		CompareHeightMaps.addActionListener(this);
		comparCSV.addActionListener(this);
		mainScreen.addActionListener(this);
		Generate3D.addActionListener(this);
		about.addActionListener(this);

		View.add(swap);

		MExport.add(mainCSV);
		CExport.add(comparCSV);

		mainFile.add(loadImage);
		mainFile.add(drawGraph);
		mainFile.add(CompareHeightMaps);
		mainFile.add(MExport);
		mainFile.add(mexit);

		comparisonFile.add(mainScreen);
		comparisonFile.add(CExport);
		comparisonFile.add(cexit);

		bar.add(mainFile);
		bar.add(View);
		bar.add(about);

		this.setJMenuBar(bar);

		// end of menu bar

		imageForm = new ImageDrawer(this);
		graphForm = new GraphContainer(this.getPreferredSize());
		mapList = new HeightMapList(this);
		compScreen = new ComparisonScreen();
		// this.getContentPane().add(compScreen, BorderLayout.CENTER);
		// compScreen.setVisible(false);

		imageForm.setLocation(this.getWidth() - imageForm.getWidth(), 0);
		mapList.setLocation(0, this.getHeight() / 2);

		this.add(imageForm, BorderLayout.CENTER);

		rightContainer = new JPanel();
		rightContainer.setLayout(new BorderLayout());

		rightContainer.add(graphForm, BorderLayout.NORTH);
		rightContainer.add(mapList, BorderLayout.CENTER);
		this.add(rightContainer, BorderLayout.WEST);

		chooser = new JFileChooser();

		this.setVisible(true);
	}

	/**
	 * Changes the active map to the newly selected one
	 * 
	 * @param map
	 *            The map
	 */
	public void setActiveMap(Data.HeightMap map) {
		if (currentActiveMap == null)
			currentActiveMap = new ActiveMap(map);
		else
			currentActiveMap.setMap(map);
		imageForm.setHMap(currentActiveMap);
	}

	/**
	 * Draws the graph
	 */
	public void drawGraph() {
		Data.ActiveMap map = imageForm.getCurrentHeightMap();
		if (map != null) {
			if (map.listOfPoints.size() > 1)
				graphForm.createChart(map, mapList.getAllMaps());
		}
	}

	/**
	 * Checks if the selected map being displayed was the deleted one
	 * 
	 * @param map
	 *            The deleted map
	 */
	public void checkDelete(Data.HeightMap map) {
		if (map != null) {
			if (currentActiveMap.map.equals(map)) {
				Vector<Data.HeightMap> maps = mapList.getAllMaps();

				if (maps.size() > 1) {
					for (int i = 0; i < maps.size(); ++i) {
						if (!maps.elementAt(i).equals(map)) {
							currentActiveMap.setMap(maps.elementAt(i));
							imageForm.setHMap(currentActiveMap);
							break;
						}
					}
				} else {
					imageForm.setHMap(null);
					currentActiveMap = null;
				}
			}
		}
	}

	/**
	 * Checks if the map being loaded is not already loaded
	 * 
	 * @param map
	 *            The map being loaded
	 * @return If it has not been added yet
	 */
	private boolean notAlreadyAdded(Data.HeightMap map) {

		for (Data.HeightMap cMap : mapList.getAllMaps()) {
			if (cMap.getPath().equals(map.getPath())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * resets layouts - this is for non comparison setting
	 */
	private void resetLayouts() {
		if (graphInRight) {

			rightContainer.remove(graphForm);
			this.remove(imageForm);

			rightContainer.add(imageForm, BorderLayout.NORTH);

			this.add(graphForm, BorderLayout.CENTER);

			graphInRight = false;
		} else {

			rightContainer.remove(imageForm);
			this.remove(graphForm);

			rightContainer.add(graphForm, BorderLayout.NORTH);

			this.add(imageForm, BorderLayout.CENTER);

			graphInRight = true;
		}
	}

	/**
	 * Reset layouts - resets layout to comparison screen
	 * 
	 * @param maps
	 *            The list of maps to compare
	 */
	public void changeLayout(Vector<Data.HeightMap> maps) {
		bar.remove(mainFile);
		bar.remove(View);
		bar.remove(about);

		bar.add(comparisonFile);
		bar.add(Generate3D);
		bar.add(about);

		mapList.setVisible(false);
		imageForm.setVisible(false);
		graphForm.setVisible(false);

		this.remove(imageForm);
		this.remove(rightContainer);

		this.add(compScreen, BorderLayout.CENTER);

		compScreen.setVisible(true);

		compScreen.setData(maps);

		if (currentActiveMap.listOfPoints.size() > 1)
			compScreen.createChart(currentActiveMap);

		repaint();
	}

	/**
	 * Changes the layout to the correct settings (JMenuBar)
	 */
	public void changeLayout() {
		bar.remove(comparisonFile);
		bar.remove(Generate3D);
		bar.remove(about);

		bar.add(mainFile);
		bar.add(View);
		bar.add(about);

		this.remove(compScreen);
		if (graphInRight)
			this.add(imageForm, BorderLayout.CENTER);
		else
			this.add(graphForm, BorderLayout.CENTER);
		this.add(rightContainer, BorderLayout.WEST);

		mapList.setVisible(true);
		imageForm.setVisible(true);
		graphForm.setVisible(true);
		compScreen.setVisible(false);

		repaint();
	}

	/**
	 * Outputs the graphs as CSV file
	 */
	private void CSV() {
		try {
			if (mapList.getAllMaps().size() > 0
					&& currentActiveMap.listOfPoints.size() > 0) {
				int fd = chooser.showSaveDialog(this);

				if (fd == JFileChooser.APPROVE_OPTION) {
					boolean success = (new File(chooser.getSelectedFile()
							.getAbsolutePath())).mkdir();

					if (success) {
						String path = chooser.getSelectedFile()
								.getAbsolutePath();

						String CSVoutput = "";
						boolean first = true;

						for (Data.HeightMap map : mapList.getAllMaps()) {
							if (!first)
								CSVoutput += "\n";

							File img = new File(path + "/" + map.getName()
									+ "-HeatMap.jpg");
							ImageIO.write(map.getImage(), "jpg", img);

							CSVoutput += map.getName() + "\n";

							CSVoutput += "X-Values: " + map.getMinX() + " to "
									+ map.getMaxX() + "\n";
							CSVoutput += "Y-Values: " + map.getMinY() + " to "
									+ map.getMaxY() + "\n";
							CSVoutput += "Height-Values: " + map.getMinHeight()
									+ " to " + map.getMaxHeight() + "\n";

							CSVoutput += "X-Distance\tImagePos\tHeight\n";
							Vector<Point> pnts = currentActiveMap.listOfPoints;
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

							first = false;
						}
						ImageIO.write(graphForm.getGraph(), "jpg", new File(
								path + "/Graph.jpg"));

						FileWriter writer = new FileWriter(path
								+ "/GraphCSV.csv");

						writer.write(CSVoutput);

						writer.close();
					} else {
						JOptionPane
								.showMessageDialog(
										this,
										"Im sorry, but you do not have the apropriate permissions to save here.\n"
												+ "Please contact your administrator to rectify this.",
										"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				JOptionPane.showMessageDialog(this,
						"There is nothing to save.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is the action listener for the jMenu Bar
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(loadImage)) {
			int fd = chooser.showOpenDialog(this);
			if (fd == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file != null) {
					HeightMap map = Data.ImageLoader.loadHeightMap(file);
					if (map != null) {
						if (notAlreadyAdded(map)) {
							if (currentActiveMap == null) {
								setActiveMap(map);
							}

							mapList.addImage(map);
						} else
							JOptionPane.showMessageDialog(this,
									"This file has already been added.",
									"Error", JOptionPane.ERROR_MESSAGE);
					} else
						JOptionPane
								.showMessageDialog(
										this,
										"The file could not be identified as a Height Map file.\nPlease try again.",
										"Error", JOptionPane.ERROR_MESSAGE);
					drawGraph();
				}
			}
		} else if (e.getSource().equals(drawGraph)) {
			drawGraph();
		} else if (e.getSource().equals(mexit) || e.getSource().equals(cexit)) {
			System.exit(0);
		} else if (e.getSource().equals(swap)) {
			resetLayouts();
		} else if (e.getSource().equals(CompareHeightMaps)) {
			Vector<Data.HeightMap> maps = mapList.getAllMaps();

			if (maps.size() <= 1) {
				JOptionPane.showMessageDialog(this,
						"Requires at least 2 images.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else if (maps.size() > 2) {
				new SelectPage(maps, this);
			} else {
				changeLayout(maps);
			}
		} else if (e.getSource().equals(mainScreen)) {
			changeLayout();
		} else if (e.getSource().equals(Generate3D)) {
			compScreen.create3DMap();
		} else if (e.getSource().equals(mainCSV)) {
			CSV();
		} else if (e.getSource().equals(comparCSV)) {
			compScreen.CSV();
		} else if (e.getSource().equals(about)) {
			try {
				Desktop.getDesktop().open(new File("About/about.html"));
			} catch (Exception e2) {
				JOptionPane
						.showMessageDialog(
								this,
								"Failed to open about webpage.\nPlease make sure you have a web browser installed.",
								"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
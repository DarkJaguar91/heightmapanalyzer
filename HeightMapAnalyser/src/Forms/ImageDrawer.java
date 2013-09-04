package Forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import Data.ActiveMap;

/**
 * 
 * @author Brandon James Talbot
 * 
 *         Contains the drawing pain and active maps
 */

public class ImageDrawer extends JInternalFrame implements ActionListener {
	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	private DrawingPane label;
	private JButton xAxis, yAxis, freeForm, LeftDiagonal, rightDiagonal;
	private JLabel cursorPoint;
	private Color usual;
	
	
	/**
	 * Constructor
	 * 
	 * @param mForm
	 *            Main form
	 */
	public ImageDrawer(MainForm mForm) {
		super("Image Drawer");

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		// setResizable(true);

		label = new DrawingPane(mForm, this);

		setLayout(new BorderLayout());

		// tool menu section
		JPanel toolMenu = new JPanel();

		toolMenu.setLayout(new FlowLayout(FlowLayout.LEADING));

		xAxis = new JButton(new ImageIcon("Images/horiz.png"));
		yAxis = new JButton(new ImageIcon("Images/vert.png"));
		freeForm = new JButton(new ImageIcon("Images/freeForm.png"));
		LeftDiagonal = new JButton(new ImageIcon("Images/rightDiag.png"));
		rightDiagonal = new JButton(new ImageIcon("Images/LeftDiag.png"));

		usual = xAxis.getBackground();
		
		refreshButtons();
		
		freeForm.setBackground(Color.BLUE);
		
		cursorPoint = new JLabel("Cursor pos: (x,x)");

		xAxis.addActionListener(this);
		yAxis.addActionListener(this);
		freeForm.addActionListener(this);
		LeftDiagonal.addActionListener(this);
		rightDiagonal.addActionListener(this);

		toolMenu.add(xAxis);
		toolMenu.add(yAxis);
		toolMenu.add(freeForm);
		toolMenu.add(LeftDiagonal);
		toolMenu.add(rightDiagonal);
		toolMenu.add(cursorPoint);

		add(toolMenu, BorderLayout.NORTH);

		// end of tool menu

		Dimension screenSize = mForm.getPreferredSize();

		label.setPreferredSize(new Dimension((int) (screenSize.width * 0.5f),
				(int) (screenSize.height * 0.9f)));

		add(label, BorderLayout.CENTER);

		setVisible(true);
	}

	/**
	 * Refreshes all the buttons
	 */
	public void refreshButtons(){
		xAxis.setBackground(usual);
		yAxis.setBackground(usual);
		freeForm.setBackground(usual);
		LeftDiagonal.setBackground(usual);
		rightDiagonal.setBackground(usual);
	}
	
	/**
	 * Getters and setters (action performed)
	 */
	public void setHMap(ActiveMap map) {
		label.setImage(map);
	}

	public ActiveMap getCurrentHeightMap() {
		return label.gethMap();
	}

	public void setMousePoint(int x, int y, ActiveMap map) {
		double xf = (map.map.getMinX() + map.map.getStep() * x);
		double yf = (map.map.getMinY() + map.map.getStep() * y);
		cursorPoint.setText("Cursor pos: (" + xf + "," + yf + ")");
	}

	public void actionPerformed(ActionEvent e) {
		refreshButtons();
		if (e.getSource().equals(freeForm)) {
			label.setDrawingType(0);
			freeForm.setBackground(Color.BLUE);
		} else if (e.getSource().equals(xAxis)) {
			label.setDrawingType(1);
			xAxis.setBackground(Color.BLUE);
		} else if (e.getSource().equals(yAxis)) {
			label.setDrawingType(2);
			yAxis.setBackground(Color.BLUE);
		} else if (e.getSource().equals(LeftDiagonal)) {
			label.setDrawingType(3);
			LeftDiagonal.setBackground(Color.BLUE);
		} else if (e.getSource().equals(rightDiagonal)) {
			label.setDrawingType(4);
			rightDiagonal.setBackground(Color.BLUE);
		}
	}
}

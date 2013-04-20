package Forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import GUITools.GridLayoutVerticalMod;

/**
 * 
 * @author Brandon James Talbot
 *
 * creates a list of all available maps in order to select just 2 for comparison
 */

public class SelectPage extends JFrame implements MouseListener, ActionListener {

	/**
	 * globals
	 */
	private static final long serialVersionUID = 1L;
	JPanel pane;
	Vector<heighMapDisplay> selected;
	JButton button;
	MainForm mainForm;
	
	/**
	 * Constructor
	 * @param maps The list of maps
	 * @param mFrm Link to main form that called this
	 */
	public SelectPage (Vector<Data.HeightMap> maps, MainForm mFrm){
		super("Map Selector");

		mainForm = mFrm;
		
		this.setSize(800, 600);
		
		this.setLayout(new BorderLayout());
		
		JPanel top = new JPanel();
		JLabel label = new JLabel("Please select 2 Images:");
		
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(label);
		
		button = new JButton("Select!");
		button.addActionListener(this);
		
		pane = new JPanel();
		pane.setLayout(new GridLayoutVerticalMod(2));
		
		JScrollPane sc = new JScrollPane(pane);
		
		getContentPane().add(top, BorderLayout.NORTH);
		getContentPane().add(sc, BorderLayout.CENTER);
		getContentPane().add(button, BorderLayout.SOUTH);
		
		selected = new Vector<heighMapDisplay>();

		for (Data.HeightMap map : maps){
			addMap(map);
		}
		
		setVisible(true);
	}
	
	/**
	 * adds the map to the list
	 * @param map Map to add
	 */
	public void addMap(Data.HeightMap map) {
		if (map != null) {
			heighMapDisplay disp = new heighMapDisplay(map);
			disp.setBackground(Color.GRAY);
			disp.addMouseListener(this);
			pane.add(disp);
//			pane.setPreferredSize(new Dimension(0, (int)(Math.ceil(((double)(pane.getComponentCount())/2))*(getWidth()/2))));
		}

		repaint();
	}

	/**
	 * mouse events - getters setters - action performed overiders
	 */
	public void mouseClicked(MouseEvent e) {
		heighMapDisplay disp = (heighMapDisplay) e.getSource();
		
		if (!selected.contains(disp)){
			if (selected.size() < 2){
				disp.setBackground(Color.red);
				selected.add(disp);
			}
			else {
				selected.get(0).setBackground(Color.GRAY);
				selected.remove(0);
				disp.setBackground(Color.red);
				selected.add(disp);
			}
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if (selected.size() < 2){
			JOptionPane.showMessageDialog(this, "You have to select 2 images.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			this.setVisible(false);
			Vector<Data.HeightMap> maps = new Vector<Data.HeightMap>();
			maps.add(selected.elementAt(0).map);
			maps.add(selected.elementAt(1).map);
			mainForm.changeLayout(maps);
		}
	}
}

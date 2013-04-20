package Forms;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import Data.HeightMap;
import GUITools.GridLayoutVerticalMod;

/**
 * 
 * @author Brandon James Talbot
 *
 * Creates a list of all the maps loaded into the program
 */

public class HeightMapList extends JInternalFrame implements MouseListener,
		ActionListener {
	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	private MainForm parent;
	private JPopupMenu popMenu;
	private heighMapDisplay currentRightClick = null;
	private JMenuItem reName, delete;
	private JPanel pane;
	
	/**
	 * Constructor
	 * @param parent The parent form (Main form)
	 */
	public HeightMapList(MainForm parent) {
		super("Image List");

		this.parent = parent;

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setPreferredSize(new Dimension((int) (screenSize.width * 0.35f),
				0));
		
		pane = new JPanel();
		
		pane.setLayout(new GridLayoutVerticalMod(2));
		
		JScrollPane sc = new JScrollPane(pane);
		
		getContentPane().add(sc);
		
		
		// popup menu settup.
		popMenu = new JPopupMenu();
		reName = new JMenuItem("Rename");
		delete = new JMenuItem("Delete");

		reName.addActionListener(this);
		delete.addActionListener(this);

		popMenu.add(reName);
		popMenu.add(delete);

		setVisible(true);
	}

	/**
	 * Adds an image to the list
	 * @param map the map to add
	 */
	public void addImage(Data.HeightMap map) {
		if (map != null) {
			heighMapDisplay disp = new heighMapDisplay(map);
			disp.addMouseListener(this);
			pane.add(disp);
//			pane.setPreferredSize(new Dimension(0, (int)(Math.ceil(((double)(pane.getComponentCount())/2))*(getWidth()/2))));
		}

		rePaint();
	}

	/**
	 * override of repaint
	 * done this way to make sure it does repaint 
	 */
	private void rePaint(){
		this.repaint();
		this.updateUI();		
	}
	
	/**
	 * Gets all the maps in the list
	 * @return Vector of maps
	 */
	public Vector<Data.HeightMap> getAllMaps() {

		Vector<Data.HeightMap> out = new Vector<HeightMap>();
		for (int i = 0; i < pane.getComponentCount(); ++i) {
			Component p = pane.getComponent(i);
			if (p instanceof heighMapDisplay) {
				out.add(((heighMapDisplay) p).getMap());
			}
		}

		return out;
	}

	/**
	 * Mouse events and getters/setters
	 */
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getModifiers() == InputEvent.BUTTON3_MASK) {
			currentRightClick = (heighMapDisplay) arg0.getSource();
			popMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		}
		if (arg0.getModifiers() == InputEvent.BUTTON1_MASK)
			parent.setActiveMap(((heighMapDisplay) arg0.getSource()).getMap());
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(delete)){
			this.parent.checkDelete(currentRightClick.map);
			pane.remove(currentRightClick);
			pane.setPreferredSize(new Dimension(0, (int)(Math.ceil(((double)(pane.getComponentCount())/2))*(getWidth()/2))));
			parent.drawGraph();
		}
		else if (e.getSource().equals(reName)){
			String newName = JOptionPane.showInputDialog(parent, "Enter new Name:", 
					 currentRightClick.getHeightMapName(), 1);
			
			if (newName != null){
				currentRightClick.setHeightMapName(newName);
			}
			
			parent.drawGraph();
		}
		rePaint();
	}
}

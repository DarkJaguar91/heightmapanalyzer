package Forms;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * @author Brandon James Talbot
 *
 * Contains all data for display purposes
 * For a specified Height map
 */

public class heighMapDisplay extends JPanel{
	/**
	 * Globals
	 */
	private static final long serialVersionUID = 1L;
	Data.HeightMap map;
	JLabel label;
	
	/**
	 * Constructor
	 * @param map The map to create the data value
	 */
	public heighMapDisplay(Data.HeightMap map)
	{
		this.map = map;
		this.setLayout(new BorderLayout());
		
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		label = new JLabel(map.getName());
		JLabel image = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(map.getImage().getSource())));
		image.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JLabel Heights = new JLabel("Height: " + Math.round(map.getMinHeight()) + " to " + Math.round(map.getMaxHeight()));
		JLabel Xvals = new JLabel("X-Axis: " + map.getMinX() + " to " + map.getMaxX());
		JLabel yVals = new JLabel("Y-Axis: " + map.getMinY() + " to " + map.getMaxY());
		
		this.add(image, BorderLayout.CENTER);
		
		Box box = Box.createVerticalBox();
		
		box.add(label);
		box.add(Heights);
		box.add(Xvals);
		box.add(yVals);
		
		this.add(box, BorderLayout.SOUTH);		
	}
	
	/**
	 * Getters and setters
	 */
	public Data.HeightMap getMap()
	{
		return map;
	}
	public void setHeightMapName(String name)
	{
		map.setName(name);
		this.label.setText(name);
	}
	public String getHeightMapName()
	{
		return map.getName();
	}
}

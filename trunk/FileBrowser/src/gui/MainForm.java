package gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class MainForm extends JFrame{
	
	
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3728756035324658823L;
	
	/**
	 * Constructor to create frame without a menu bar
	 * @param name The name for the Title of the frame
	 */
	public MainForm(String name){
		this(name, null);
	}
	
	/**
	 * Constructor to create frame with a menu bar
	 * @param name The title for the frame
	 * @param menuBar The menu bar to use (if null no menu bar will be set)
	 */
	public MainForm(String name, JMenuBar menuBar){
		super(name);
		
		setSize(640, 480);
		setLocationRelativeTo(null);
	
		if (menuBar != null)
			setJMenuBar(menuBar);
	
		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	/**
	 * Sets the content of the form or empties it if no compenent is sent.
	 * @param obj Component to set as frame content (if null it clears the frame)
	 */
	public void setContent(Component obj){
		this.getContentPane().removeAll();
		this.getContentPane().add(obj, BorderLayout.CENTER);
		this.getContentPane().repaint();
	}
}

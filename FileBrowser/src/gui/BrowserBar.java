package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import controller.BrowserController;

public class BrowserBar extends JMenuBar implements ActionListener {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -7018275420909678122L;

	protected JMenuItem selectFolder, exit, about;
	protected BrowserController controller;

	public BrowserBar(BrowserController controller) {
		this.controller = controller;
		
		// create menu items
		JMenu file = new JMenu("File");
		file.add((selectFolder = new JMenuItem("Add Folder")));
		file.add((exit = new JMenuItem("Exit")));
		
		JMenu help = new JMenu("Help");
		help.add((about = new JMenuItem("About")));
		
		// add menu items
		this.add(file);
		this.add(help);

		// action listeners
		selectFolder.addActionListener(this);
		exit.addActionListener(this);
		about.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(exit)){
			System.exit(0);
		}
		else if (e.getSource().equals(about)){
			controller.showAbout();
		}
		else if (e.getSource().equals(selectFolder)){
			controller.addFolder();
		}
	}

}

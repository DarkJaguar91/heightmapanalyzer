package controller;

import gui.BrowserBar;
import gui.FileDisplay;
import gui.MainForm;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import layout.GridLayoutVerticalMod;

public class BrowserController {
	
	protected JMenuBar menuBar;
	protected MainForm browserForm;
	protected JTabbedPane folderPane;
	protected JFileChooser fileChooser;
	
	
	public BrowserController(){
		// create fileChooser
		fileChooser = new JFileChooser();
		
		// create the menu bar
		menuBar = new BrowserBar(this);
		
		// create mainFrame
		browserForm = new MainForm("Browser", menuBar);
		
		// set main container for mainForm
		folderPane = new JTabbedPane();
		browserForm.setContent(folderPane);
	}
	
	public void addFolder(){
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = fileChooser.showOpenDialog(browserForm);
		
		if (option == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			
			JPanel pane = new JPanel();
			pane.setLayout(new GridLayoutVerticalMod(1));
			
			JScrollPane scrollPane = new JScrollPane(pane);
			
			for (File f : file.listFiles()){
				FileDisplay display = new FileDisplay(f);
				
				pane.add(display);
			}
			
			folderPane.add(file.getName(), scrollPane);
			folderPane.updateUI();
		}
	}
	
	public void showAbout(){
		// TODO - Show about frame
	}
}

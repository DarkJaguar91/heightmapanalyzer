package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.FileSizeCulculator;

public class FileDisplay extends JPanel implements MouseListener{
	
	/**
	 * Serial UID 
	 */
	private static final long serialVersionUID = -9022283305279167213L;
	File file;
	JLabel size;
	double sizeValue = 0;
	Thread asyncThread = null;
	
	public FileDisplay(File file){
		if (file == null)
			throw new NullPointerException("NULL file sent to FileDiplay class");
		
		this.file = file;
		
		size = new JLabel("Calculating...");
		calcFileSizeAsynch();

		JLabel nameL = new JLabel("Name: ");
		JLabel sizeL = new JLabel("Size: ");
		JLabel typeL = new JLabel("Type: ");
		
		setBorderForFile();
		
		Box labelBox = Box.createVerticalBox();
		Box contentBox = Box.createVerticalBox();
		Box boxContainer = Box.createHorizontalBox();
		
		labelBox.add(nameL);
		labelBox.add(typeL);
		labelBox.add(sizeL);
		
		contentBox.add(new JLabel(getName()));
		contentBox.add(new JLabel(getExtension()));
		contentBox.add(size);
		
		boxContainer.add(labelBox);
		boxContainer.add(contentBox);
		
		this.setLayout(new BorderLayout());
		this.add(boxContainer, BorderLayout.CENTER);
	}
	
	private void setBorderForFile(){
		if (file.isFile()){
			this.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		}
		else {
			this.setBorder(BorderFactory.createLineBorder(Color.orange));
		}
	}
	
	private Runnable sizeRunner = new Runnable() {
		@Override
		public void run() {
			sizeValue = FileSizeCulculator.calculateSize(file);
			size.setText(FileSizeCulculator.convertToString(sizeValue));
			updateUI();
		}
	};
	
	private void calcFileSizeAsynch(){
		Thread t = new Thread(sizeRunner);
		
		t.start();
	}

	public String getName(){
		String name = file.getName();
		if (name.contains(".")){
			name = name.substring(0, name.lastIndexOf('.'));
		}
		return name;
	}
	
	public String getExtension(){
		if (file.isFile()){
			return file.getName().substring(file.getName().lastIndexOf('.') + 1);
		}
		else return "Folder";
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}

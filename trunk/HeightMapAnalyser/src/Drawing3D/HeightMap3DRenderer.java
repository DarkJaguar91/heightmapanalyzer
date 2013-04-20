package Drawing3D;

import static javax.media.opengl.GL.GL_CLEAR;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_COLOR_LOGIC_OP;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL2.GL_COMPILE;
import static javax.media.opengl.GL2.GL_QUADS;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_FILL;
import static javax.media.opengl.GL2GL3.GL_LINE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import Data.HeightMap;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
// GL constants
// GL2 constants

public class HeightMap3DRenderer extends GLJPanel implements GLEventListener,
				MouseMotionListener,MouseListener,MouseWheelListener,ActionListener,ChangeListener{
	/**
	 * serial
	 */
	private static final long serialVersionUID = -7011190352085848963L;
	static final int FPS = 30;
	static final double gradientStart = 0.05;
	static final FPSAnimator animator = new FPSAnimator(FPS, true);
	public enum fillOption { FO_BOTH, FO_WIRE, FO_FILL}; 
	//rendering setup
	fillOption currentFill = fillOption.FO_FILL;
	//Camera Variables
	float zoomFactor = 500;
	float angleX = 130;
	float angleY = -15;
	float focusX,focusY,focusZ;
	float zNear = 0.1f;
	float zFar = 10000;
	float heightScale = 5f;
	int wantTicksX = 20;
	int wantTicksH = 5;
	int wantTicksY = 25;
	int wantMinorTicksX = 5;
	int wantMinorTicksH = 5;
	int wantMinorTicksY = 5;
	double majorTickIntervalX;
	double majorTickIntervalH;
	double majorTickIntervalY;
	int minorTickLength = 2;
	int majorTickLength = 6;
	int textOffsetFromAxis = 15;
	double textScale = 0.03;
	double percentOfTextScale = 1;
	//control variables
	boolean isMouseRightDown = false;
	boolean isMouseLeftDown = false;
	int prevX, prevY;
	int currentX, currentY;
	//DisplayListSetup
	int displayList;
	int axisList;
	boolean mustRecomputeDisplayList = false;
	boolean mustRecomputeAxisList = false;
	Vector<HeightMap> heightmapList = new Vector<HeightMap>();
	int interpolationSteps = 0;
	int threadsToComputeInterpolation = 4;
	Vector<Thread> threadList = new Vector<Thread>();
	Vector<InterpolatedDataPt> computedData = new Vector<InterpolatedDataPt>();
	//Axis variables:
	double globalMinX = 0;
	double globalMinY = 0;
	double globalMaxX = 0;
	double globalMaxY = 0;
	double globalMaxH = 0;
	double globalMinH = 0;
	double axisOffset = 0;
	//Pin Setup:
	double[] projectionMatrix = new double[16];
	double[] viewMatrix = new double[16];
	int[] viewport = new int[4];
	double XI,YI,ZI;
	double[] worldspaceCoordNear = new double[4];
	double[] worldspaceCoordFar = new double[4];
	/*********************************************************************************
	 * MENUS	
	 *********************************************************************************/
	JPopupMenu mbrMain;
	JMenu mnuOptions = new JMenu("Options");
	JMenu mnuRendering = new JMenu("Rendering");
	JMenu mnuInterpolation = new JMenu("Interpolation Quality");
	JMenu mnuRenderMode = new JMenu("Render Mode");
	JMenu mnuAxis = new JMenu("Axis");
	JMenu mnuFontSize = new JMenu("Tick Font Size");
	JMenu mnuTicksOnXAxis = new JMenu("X axis");
	JMenu mnuTicksOnYAxis = new JMenu("Y axis");
	JMenu mnuTicksOnZAxis = new JMenu("Z axis");
	JMenu mnuMinorTicksOnXAxis = new JMenu("X axis");
	JMenu mnuMinorTicksOnYAxis = new JMenu("Y axis");
	JMenu mnuMinorTicksOnZAxis = new JMenu("Z axis");
	JMenu mnuMinor = new JMenu("Minor Ticks");
	JMenu mnuMajor = new JMenu("Major Ticks");
	JMenu mnuZScale = new JMenu("Height Scale");
	JRadioButton mitRenderModeFill = new JRadioButton("Fill",true);
	JRadioButton mitRenderModeWire = new JRadioButton("Wire Frame");
	JRadioButton mitRenderModeBoth = new JRadioButton("Both");
	JMenuItem mitSnapshot = new JMenuItem("Take snapshot");
	//JMenuItem mitCloseWindow = new JMenuItem("Close Window");
	JSlider jslAxisFontSize = new JSlider();
	JSlider jslZScale = new JSlider();
	JSpinner jspInterpolationQuality = new JSpinner();
	JSpinner jspNumXTicks = new JSpinner();
	JSpinner jspNumZTicks = new JSpinner();
	JSpinner jspNumHTicks = new JSpinner();
	JSpinner jspNumMinorXTicks = new JSpinner();
	JSpinner jspNumMinorZTicks = new JSpinner();
	JSpinner jspNumMinorHTicks = new JSpinner();
	/**
	 * Sets up the menus
	 */
	private void setupMenus(){
		mbrMain.add(mnuOptions);
		mbrMain.add(mnuRendering);
		mnuRendering.add(mnuInterpolation);
		mnuInterpolation.add(jspInterpolationQuality);
		SpinnerNumberModel sp = new SpinnerNumberModel();
		sp.setMinimum(0);
		jspInterpolationQuality.setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspInterpolationQuality.setModel(sp);
		jspInterpolationQuality.addChangeListener(this);
		jspInterpolationQuality.setVerifyInputWhenFocusTarget(true);
		jspInterpolationQuality.setPreferredSize(new Dimension(60,20));
		mnuRendering.add(mnuRenderMode);
		mnuRenderMode.add(mitRenderModeFill);
		mnuRenderMode.add(mitRenderModeWire);
		mnuRenderMode.add(mitRenderModeBoth);
		mitRenderModeFill.addActionListener(this);
		mitRenderModeWire.addActionListener(this);
		mitRenderModeBoth.addActionListener(this);
		
		mnuOptions.add(mnuAxis);
		mnuOptions.add(mitSnapshot);
		mitSnapshot.addActionListener(this);
		mnuAxis.add(mnuFontSize);
		mnuAxis.add(mnuMinor);
		mnuAxis.add(mnuMajor);
		mnuMajor.add(mnuTicksOnXAxis);
		mnuMajor.add(mnuTicksOnYAxis);
		mnuMajor.add(mnuTicksOnZAxis);
		mnuMinor.add(mnuMinorTicksOnXAxis);
		mnuMinor.add(mnuMinorTicksOnYAxis);
		mnuMinor.add(mnuMinorTicksOnZAxis);
		jslAxisFontSize.setMinimum(50);
		jslAxisFontSize.setMaximum(400);
		jslAxisFontSize.setValue(100);
		jslAxisFontSize.setMajorTickSpacing(15);
		jslAxisFontSize.addChangeListener(this);
		jslZScale.setMinimum(50);
		jslZScale.setMaximum(10000);
		jslZScale.setValue((int)getZScale()*100);
		jslZScale.setMajorTickSpacing(5);
		jslZScale.addChangeListener(this);
		mnuFontSize.add(jslAxisFontSize);
		mnuZScale.add(jslZScale);
		mnuRendering.add(mnuZScale);
		mnuTicksOnXAxis.add(jspNumXTicks);
		//X -ticks
		SpinnerNumberModel sp1 = new SpinnerNumberModel();
		sp1.setMinimum(1);
		jspNumXTicks.setModel(sp1);
		jspNumXTicks.setPreferredSize(new Dimension(60,20));
		jspNumXTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumXTicks.setValue(getNumTicksX());
		jspNumXTicks.addChangeListener(this);
		
		
		mnuTicksOnYAxis.add(jspNumHTicks);
		//Y ticks
		SpinnerNumberModel spY = new SpinnerNumberModel();
		spY.setMinimum(1);
		jspNumHTicks.setModel(spY);
		jspNumHTicks.setPreferredSize(new Dimension(60,20));
		jspNumHTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumHTicks.setValue(getNumTicksZ());
		jspNumHTicks.addChangeListener(this);
		
		//Z ticks
		SpinnerNumberModel sp2 = new SpinnerNumberModel();
		sp2.setMinimum(1);
		mnuTicksOnZAxis.add(jspNumZTicks);
		jspNumZTicks.setModel(sp2);
		jspNumZTicks.setPreferredSize(new Dimension(60,20));
		jspNumZTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumZTicks.setValue(getNumTicksH());
		jspNumZTicks.addChangeListener(this);
		
		//MINOR TICKS:
		
		mnuMinorTicksOnXAxis.add(jspNumMinorXTicks);
		//X -ticks
		SpinnerNumberModel spM1 = new SpinnerNumberModel();
		spM1.setMinimum(1);
		jspNumMinorXTicks.setModel(spM1);
		jspNumMinorXTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorXTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorXTicks.setValue(getNumMinorTicksX());
		jspNumMinorXTicks.addChangeListener(this);
		
		
		mnuMinorTicksOnYAxis.add(jspNumMinorHTicks);
		//Y ticks
		SpinnerNumberModel spMY = new SpinnerNumberModel();
		spMY.setMinimum(1);
		jspNumMinorHTicks.setModel(spMY);
		jspNumMinorHTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorHTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorHTicks.setValue(getNumMinorTicksZ());
		jspNumMinorHTicks.addChangeListener(this);
		
		//Z ticks
		SpinnerNumberModel spM2 = new SpinnerNumberModel();
		spM2.setMinimum(1);
		mnuMinorTicksOnZAxis.add(jspNumMinorZTicks);
		jspNumMinorZTicks.setModel(spM2);
		jspNumMinorZTicks.setPreferredSize(new Dimension(60,20));
		jspNumMinorZTicks.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent arg0) {
				return (((JSpinner)arg0).getValue().toString().matches("[0-9]+"));
			}
		});
		jspNumMinorZTicks.setValue(getNumMinorTicksH());
		jspNumMinorZTicks.addChangeListener(this);
		
		//finally add close window:
		//mnuOptions.add(new JSeparator());
		//mnuOptions.add(mitCloseWindow);
		//mitCloseWindow.addActionListener(this);
		
		
	}
	/**
	 * Stores an interpolated data set
	 * @author benna
	 *
	 */
	public class InterpolatedDataPt{
		public int startX, startY;
		public double[][] intermediate;
		public InterpolatedDataPt(int startX,int startY,double[][] intermediate){
			this.startX = startX;
			this.startY = startY;
			this.intermediate = intermediate;
		}
	}
	
	public HeightMap3DRenderer(){
		//mbrMain.setLightWeightPopupEnabled(false);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.mbrMain = new JPopupMenu();
		add(mbrMain);
		this.setupMenus();
		this.addGLEventListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		
		final GLJPanel me = this;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				animator.add(me);
				animator.start();
			}
		});
	}
	public synchronized void addHeightmap(HeightMap heightmap){
		if (heightmapList.size() == 2){
			JOptionPane.showMessageDialog(this,
				    "Cannot render more than two heightmaps. Please specify a maximum of two input files",
				    "Validation",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		heightmapList.add(heightmap);
		mustRecomputeDisplayList = true;
		mustRecomputeAxisList = true;
	}
	public synchronized void setInterpolation(int value){
		if (value >= 0){
			this.interpolationSteps = value;
			mustRecomputeDisplayList = true;
		}
	}
	public synchronized int getHeightmapCount(){
		return heightmapList.size();
	}
	public synchronized void removeHeightmap(int i){
		heightmapList.remove(i);
		mustRecomputeDisplayList = true;
		mustRecomputeAxisList = true;
	}
	public synchronized void setCurrentFill(fillOption op){
		currentFill = op;
	}
	public synchronized void setNumTicksX(int x){
		this.wantTicksX = x;
		this.mustRecomputeAxisList = true;
	}
	public synchronized void setNumTicksZ(int z){
		this.wantTicksY = z;
		this.mustRecomputeAxisList = true;
	}
	public synchronized void setNumTicksH(int h){
		this.wantTicksH = h;
		this.mustRecomputeAxisList = true;
	}
	public synchronized int getNumTicksX(){
		return this.wantTicksX;
	}
	public synchronized int getNumTicksH(){
		return this.wantTicksH;
	}
	public synchronized int getNumTicksZ(){
		return this.wantTicksY;
	}
	public synchronized void setNumMinorTicksX(int x){
		this.wantMinorTicksX = x;
		this.mustRecomputeAxisList = true;
	}
	public synchronized void setNumMinorTicksZ(int z){
		this.wantMinorTicksY = z;
		this.mustRecomputeAxisList = true;
	}
	public synchronized void setNumMinorTicksH(int h){
		this.wantMinorTicksH = h;
		this.mustRecomputeAxisList = true;
	}
	public synchronized int getNumMinorTicksX(){
		return this.wantMinorTicksX;
	}
	public synchronized int getNumMinorTicksH(){
		return this.wantMinorTicksH;
	}
	public synchronized int getNumMinorTicksZ(){
		return this.wantMinorTicksY;
	}
	public synchronized void setTextZoom(double value){
		percentOfTextScale = value;
	}
	public synchronized void setZScale(float scale){
		heightScale = scale;
	}
	public synchronized float getZScale(){
		return heightScale;
	}
	/**
	 * Computes the 3D rendering
	 * @param gl
	 * @param bFirstRun Specifies if this is the first time the display list is being created
	 */
	private synchronized void compute3DRendering(GL2 gl, boolean bFirstRun){
		//Delete previous display list if any exists:
		if (! bFirstRun)
		{
			gl.glDeleteLists(displayList, 1);
			displayList = gl.glGenLists(1);
		}
		//Compute interpolated heightmaps:
		if (heightmapList.size() > 0)
		{
			globalMinX = heightmapList.firstElement().getMinX();
			globalMinY = heightmapList.firstElement().getMinY();
			globalMaxX = heightmapList.firstElement().getMaxX();
			globalMaxY = heightmapList.firstElement().getMaxY();
			globalMaxH = heightmapList.firstElement().getMaxHeight();
			globalMinH = heightmapList.firstElement().getMinHeight();
		}
		int numHeightmapsRendered = 0;
		gl.glNewList(displayList, GL_COMPILE);
		for(HeightMap h : heightmapList){
			globalMinX = Math.min(globalMinX, h.getMinX());
			globalMinY = Math.min(globalMinY, h.getMinY());
			globalMaxX = Math.max(globalMaxX, h.getMaxX());
			globalMaxY = Math.max(globalMaxY, h.getMaxY());
			globalMaxH = Math.max(h.getMaxHeight(),globalMaxH);
			globalMinH = Math.min(h.getMinHeight(),globalMinH);

			int y = h.getData().length;
			int x = h.getData()[0].length;
			double centerX = (h.getMaxX()+h.getMinX())*0.5f;
			double centerY = (h.getMaxY()+h.getMinY())*0.5f;
			double step = h.getStep()/(interpolationSteps+1);
			int blockSizePerThread = Math.max(y,x) / 4;

//			int loopYUpper = y/blockSizePerThread - 1;
//			int loopXUpper = x/blockSizePerThread - 1;

			//Parallelize this embarrassingly parallel problem:
			for (int i = 0; i < y - 1; i += blockSizePerThread )
				for (int j = 0; j < x - 1; j += blockSizePerThread){
					computeSubset(j, Math.min(blockSizePerThread + j,x-1),
							i, Math.min(blockSizePerThread + i,y-1),h,step,
							centerX,centerY);
				}
			//Wait for the threads to join up again:
			for (Thread t:threadList){
				try{
					t.join();
				} catch (InterruptedException e) {};
			}
			threadList.clear();

			//Now save to display list:
			for (InterpolatedDataPt pt : this.computedData){
				for (int k = 0; k < interpolationSteps + 1; ++k){									//Draw interpolated map
					gl.glBegin(GL_TRIANGLE_STRIP); // draw using triangles
					for (int m = 0; m < interpolationSteps + 2; ++m)
					{
						double gradientPt1 = ((pt.intermediate[k][m]+h.getMinHeight())/(h.getMaxHeight()+h.getMinHeight())*(1-gradientStart) + gradientStart);
						double gradientPt2 = ((pt.intermediate[k+1][m]+h.getMinHeight())/(h.getMaxHeight()+h.getMinHeight())*(1-gradientStart) + gradientStart);
						if (numHeightmapsRendered == 0)
							gl.glColor4d(gradientPt1,0,0,0.6);
						else
							gl.glColor4d(0,gradientPt1,0,0.6);
						gl.glVertex3d(-(pt.startX*(interpolationSteps+1)+m)*step-h.getMinX(),
								pt.intermediate[k][m],
								(pt.startY*(interpolationSteps+1)+k)*step+h.getMinY());
						if (numHeightmapsRendered == 0)
							gl.glColor4d(gradientPt2,0,0,0.6);
						else
							gl.glColor4d(0,gradientPt2,0,0.6);
						gl.glVertex3d(-(pt.startX*(interpolationSteps+1)+m)*step-h.getMinX(),
								pt.intermediate[k+1][m],
								(pt.startY*(interpolationSteps+1)+k+1)*step+h.getMinY());
					} //interpolated barray width
					gl.glEnd();
				} //interpolated array height
			}
			numHeightmapsRendered++;
			this.computedData.clear();
		}	
		gl.glEndList();
		
		System.gc();
		//Return to drawing state:
		mustRecomputeDisplayList = false;
	}
	/**
	 * Computes a new set of axis and saves them to a display list
	 * @param gl
	 * @param bFirstRun Specifies if this is the first time the display list is being created
	 */
	private synchronized void computeNewAxis(GL2 gl, boolean bFirstRun){
		//Delete previous display list if any exists:
		if (! bFirstRun)
		{
			gl.glDeleteLists(axisList, 1);
			axisList = gl.glGenLists(1);
		}
		gl.glNewList(axisList, GL_COMPILE);
		
		this.majorTickIntervalX = (globalMaxX - globalMinX)/(float)this.wantTicksX;
		this.majorTickIntervalH = (globalMaxH - globalMinH)/(float)this.wantTicksH;
		this.majorTickIntervalY = (globalMaxY - globalMinY)/(float)this.wantTicksY;
		double minorTickIntervalX = this.majorTickIntervalX / (wantMinorTicksX+1);
		double minorTickIntervalH = this.majorTickIntervalH / (wantMinorTicksH+1);
		double minorTickIntervalY = this.majorTickIntervalY / (wantMinorTicksY+1);
		axisOffset = ((globalMaxX - globalMinX)*0.1f + (globalMaxY - globalMinY)*0.1f)*0.5f;
		//draw axis
		gl.glColor3f(1,1,1);
		gl.glBegin(GL.GL_LINES);

		//X:
		gl.glVertex3d(globalMaxX,0,globalMinY-axisOffset);
		gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset);
		//Height:
		gl.glVertex3d(globalMaxX,globalMaxH,globalMinY-axisOffset);
		gl.glVertex3d(globalMaxX,globalMinH,globalMinY-axisOffset);
		//Z:
		gl.glVertex3d(globalMinX-axisOffset,0,globalMaxY);
		gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset);
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i+=minorTickIntervalX){
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset);
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset - minorTickLength);
		}
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i+=minorTickIntervalH){
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset);
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset + minorTickLength);
		}
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i+=minorTickIntervalY){
			gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset + i);
			gl.glVertex3d(globalMinX-axisOffset - minorTickLength,0,globalMinY-axisOffset + i);
		}
		//Major ticks
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i+=majorTickIntervalX){
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset);
			gl.glVertex3d(globalMinX-axisOffset + i,0,globalMinY-axisOffset - majorTickLength);
		}
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i+=majorTickIntervalH){
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset);
			gl.glVertex3d(globalMaxX,globalMinH + i,globalMinY-axisOffset + majorTickLength);
		}
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i+=majorTickIntervalY){
			gl.glVertex3d(globalMinX-axisOffset,0,globalMinY-axisOffset + i);
			gl.glVertex3d(globalMinX-axisOffset - majorTickLength,0,globalMinY-axisOffset + i);
		}
		gl.glEnd();
		gl.glEndList();
		mustRecomputeAxisList = false;
	}
	/**
	 * Multi-threads interpolation, by assigning subsets to different threads  
	 * @param xLowerBound start X of subset
	 * @param xUpperBound end X of subset
	 * @param yLowerBound start Y of subset
	 * @param yUpperBound end X of subset
	 * @param h heightmap
	 * @param step step size (after interpolation has been taken into account
	 * @param centerX center X of subset
	 * @param centerY center Y of subset
	 */
	private synchronized void computeSubset(final int xLowerBound, final int xUpperBound,
			final int yLowerBound, final int yUpperBound, final HeightMap h, 
			final double step, final double centerX, final double centerY){
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (int i = yLowerBound; i < yUpperBound; ++i){						//Loop through the height of the map
					for (int j = xLowerBound; j < xUpperBound; ++j){					//Loop through the width of the map
						InterpolatedDataPt pt = new InterpolatedDataPt(j, i, SurfaceInterpolator.interpolateSurface(h.getData()[i][j],h.getData()[i+1][j],
								h.getData()[i+1][j+1],h.getData()[i][j+1],interpolationSteps));				//Compute interpolated map
						computedData.add(pt);
					} //width
				} //height
			} //run method
		});
		threadList.add(t);
		t.start();
	}
	
	/*********************************************************************************************************************************************************
	 * OPENGL SETUP SECTION
	 *********************************************************************************************************************************************************/
	
	// Setup OpenGL Graphics Renderer:
	// ------ Implement methods declared in GLEventListener ------
	private GLU glu;  // for the GL Utility
	private GLUT glut;
	/**
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	public synchronized void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
		glu = new GLU();                         // get GL Utilities
		glut = new GLUT();						//Utilities Toolkit
		gl.glClearColor(0.0f, 0.5f, 0.5f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f);      // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
		gl.glShadeModel(GL_SMOOTH);
		displayList = gl.glGenLists(1);
		axisList = gl.glGenLists(1);
		compute3DRendering(gl,true);
		computeNewAxis(gl,true);
		gl.glPointSize(5);
		gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable is
	 * first set to visible.
	 */
	public synchronized void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

		if (height == 0) height = 1;   // prevent divide by zero
		float aspect = (float)width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
		gl.glLoadIdentity();             // reset projection matrix
		glu.gluPerspective(45.0, aspect, zNear, zFar); // fovy, aspect, zNear, zFar
		
		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
	}
	/**
	 * Called back by the animator to perform rendering.
	 */
	public synchronized void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
		if (mustRecomputeDisplayList){
			this.compute3DRendering(gl, false);
		}
		if (mustRecomputeAxisList){
			this.computeNewAxis(gl,false);
		}
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
		gl.glLoadIdentity();  // reset the model-view matrix
		//Scale, Rotate and translate world:
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -zoomFactor);
			
		gl.glRotatef(angleY,-1,0,0);
		gl.glRotatef(angleX,0,1,0);
		gl.glScaled(1, this.heightScale, 1);
		
		//save the matrices' states
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, viewMatrix, 0);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		//draw heightmaps
		switch (currentFill){
		case FO_BOTH:
			gl.glLogicOp(GL_CLEAR);
			gl.glEnable(GL_COLOR_LOGIC_OP);
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
			gl.glCallList(displayList);
			gl.glDisable(GL_COLOR_LOGIC_OP);
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
			gl.glCallList(displayList);
			break;
		case FO_WIRE:
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
			gl.glCallList(displayList);
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
			break;
		case FO_FILL:
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
			gl.glCallList(displayList);
			break;
		}
		
		gl.glCallList(axisList);
		//draw pin
		gl.glColor3f(1,1,0);
		gl.glBegin(GL_LINES);
		gl.glVertex3d(XI,YI+Math.min(globalMinH,0) ,ZI);
		gl.glVertex3d(XI,YI+globalMaxH*1.2f,ZI);
		gl.glEnd();
		gl.glColor3f(1,1,1);
		gl.glBegin(GL_POINTS);
		gl.glVertex3d(XI,YI+globalMaxH*1.2f,ZI);
		gl.glVertex3d(XI,YI+Math.min(globalMinH,0) ,ZI);
		gl.glEnd();
		gl.glColor3f(1,1,0);
		
		//Draw the rotating text on the rulers:
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0");
		gl.glScaled(1, 1/this.heightScale, 1);
		//Height ruler:
		for(float i = 0; i < globalMaxH - globalMinH; i += majorTickIntervalH){
			gl.glPushMatrix();

			gl.glTranslated(globalMaxX,(i + globalMinH)*this.heightScale,globalMinY - axisOffset + textOffsetFromAxis*percentOfTextScale*0.3);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinH)));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated(globalMaxX,
				(globalMaxH+globalMinH)*0.5*heightScale,
				globalMinY - axisOffset*1.5 - textOffsetFromAxis*percentOfTextScale*0.3);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, "Z");
		gl.glPopMatrix();
		//X ruler:
		for(float i = 0; i < globalMaxX - globalMinX + axisOffset; i += majorTickIntervalX){
			gl.glPushMatrix();

			gl.glTranslated(globalMinX-axisOffset + i,0,globalMinY - axisOffset - textOffsetFromAxis*percentOfTextScale*0.9);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinX) - axisOffset));
			gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslated((globalMinX+globalMaxX-axisOffset*2)*0.5,0,globalMinY - axisOffset*1.5 - textOffsetFromAxis*percentOfTextScale*0.9);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, "X");
		gl.glPopMatrix();
		//Z ruler:
		for(float i = 0; i < globalMaxY - globalMinY + axisOffset; i += majorTickIntervalY){
			gl.glPushMatrix();

			gl.glTranslated(globalMinY - axisOffset - textOffsetFromAxis*percentOfTextScale*0.9,0,globalMinY-axisOffset + i);
			gl.glRotatef(angleX,0,-1,0);
			gl.glRotatef(angleY,1,0,0);
			gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);

			glut.glutStrokeString(GLUT.STROKE_ROMAN, df.format(i+Math.floor(globalMinX) - axisOffset));
			gl.glPopMatrix();
		}
		
		gl.glPushMatrix();
		gl.glTranslated(globalMinY - axisOffset*1.5 - textOffsetFromAxis*percentOfTextScale*0.9,0,(globalMinY+globalMaxY-axisOffset*2)*0.5);
		gl.glRotatef(angleX,0,-1,0);
		gl.glRotatef(angleY,1,0,0);
		gl.glScaled(textScale*percentOfTextScale,textScale*percentOfTextScale,textScale*percentOfTextScale);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, "Y");
		gl.glPopMatrix();
		
		//Draw text:
		df.applyPattern("0.00");
		gl.glPopMatrix();
		gl.glOrtho(-1, 1, -1, 1, -1, 1);
		gl.glTranslated(0, 0, 0.1);
		gl.glColor3f(0,0,0);
		gl.glBegin(GL_QUADS);
		gl.glVertex3f(-0.060f, -0.031f, 0);
		gl.glVertex3f(-0.060f, -0.045f, 0);
		gl.glVertex3f(0.060f, -0.045f, 0);
		gl.glVertex3f(0.060f, -0.031f, 0);
		gl.glEnd();
		gl.glColor3f(1,1,1);
		gl.glRasterPos2d(-0.055, -0.033);
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, "Pin X-Pos:"+df.format(XI));
		gl.glRasterPos2d(-0.055, -0.035);
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, "Pin Y-Pos:"+df.format(ZI));
		String height1 = "";
		String height2 = "";
		if (heightmapList.size() >= 1)
		{	
			int xCoord = (int)((-XI + heightmapList.get(0).getMaxX())/heightmapList.get(0).getStep());
			int zCoord = (int)((ZI - heightmapList.get(0).getMinY())/heightmapList.get(0).getStep());
			height1 = heightmapList.get(0).getName() +  
					((XI - heightmapList.get(0).getMinX() >= 0 && 
					XI - heightmapList.get(0).getMinX() < -heightmapList.get(0).getMinX()+heightmapList.get(0).getMaxX() &&
					ZI - heightmapList.get(0).getMinY() >= 0 && 
					ZI - heightmapList.get(0).getMinY() < -heightmapList.get(0).getMinY()+heightmapList.get(0).getMaxY()) ? 
							"["+xCoord+","+zCoord+"] : " + df.format(heightmapList.get(0).getData()[zCoord]
									[xCoord]) : " : Out of Bounds");
			
			if (heightmapList.size() >= 2){
				xCoord = (int)((-XI + heightmapList.get(1).getMaxX())/heightmapList.get(1).getStep());
				zCoord = (int)((ZI - heightmapList.get(1).getMinY())/heightmapList.get(1).getStep());
				height2 = heightmapList.get(1).getName() + 
						((XI - heightmapList.get(1).getMinX() >= 0 && 
						XI - heightmapList.get(1).getMinX() < -heightmapList.get(1).getMinX()+heightmapList.get(1).getMaxX() &&
						ZI - heightmapList.get(1).getMinY() >= 0 && 
						ZI - heightmapList.get(1).getMinY() < -heightmapList.get(1).getMinY()+heightmapList.get(1).getMaxY()) ? 
								"["+xCoord+","+zCoord+"] : " + df.format(heightmapList.get(1).getData()[zCoord]
										[xCoord]) : " : Out of Bounds");
			
			} else height2 = "Not Loaded";
		} else height1 = "Not Loaded";
		gl.glColor3f(1,0.25f,0.25f);
		gl.glRasterPos2d(-0.055, -0.037);
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, height1);
		gl.glColor3f(0.25f,1,0.25f);
		gl.glRasterPos2d(-0.055, -0.039);
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, height2);
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such as buffers.
	 */
	public synchronized void dispose(GLAutoDrawable drawable) { animator.stop(); }

	public synchronized void mouseDragged(MouseEvent arg0) {
		if (isMouseRightDown)
		{
			
		}
		else if (isMouseLeftDown){
			unproject(arg0);
			angleX += (arg0.getX()-prevX);
			angleY += (arg0.getY()-prevY);
			if (angleX > 360)
				angleX -= 360;
			else if (angleX < -360)
				angleX += 360;
			if (angleY > 360)
				angleY -= 360;
			else if (angleY < -360)
				angleY += 360;
			prevX = arg0.getX();
			prevY = arg0.getY();
		}
	}

	public synchronized void mouseMoved(MouseEvent arg0) {
		unproject(arg0);
	}
	private synchronized void unproject(MouseEvent arg0){
		float realy = viewport[3] - (int) arg0.getY() - 1;
		glu.gluUnProject(arg0.getX(),realy, 0.1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordNear,0);
		glu.gluUnProject(arg0.getX(),realy, 1,viewMatrix,0,projectionMatrix,0,viewport,0,worldspaceCoordFar,0);			

		double vX = - worldspaceCoordNear[0] + worldspaceCoordFar[0];
		double vY = - worldspaceCoordNear[1] + worldspaceCoordFar[1];
		double vZ = - worldspaceCoordNear[2] + worldspaceCoordFar[2];
		double vLength = Math.sqrt(vX*vX + vY*vY + vZ*vZ);		
		vX = vX / vLength;
		vY = vY / vLength;
		vZ = vZ / vLength;
		double t = (-worldspaceCoordNear[1])/(vY+0.000000000001); 
		XI = (worldspaceCoordNear[0] + t*vX);
		YI = 0; //XZ-Plane
		ZI = (worldspaceCoordNear[2] + t*vZ);
	}
	public synchronized void mouseClicked(MouseEvent arg0) {}

	public synchronized void mouseEntered(MouseEvent arg0) {
		isMouseRightDown = false;
		isMouseLeftDown = false;
	}

	public synchronized void mouseExited(MouseEvent arg0) {
		isMouseRightDown = false;
		isMouseLeftDown = false;
	}

	public synchronized void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
		{
			prevX = arg0.getX();
			prevY = arg0.getY();
			isMouseLeftDown = true;
		}
		else if (arg0.getButton() == MouseEvent.BUTTON3)
		{
			mbrMain.show(this,
					arg0.getX(), arg0.getY());
		}
	}

	public synchronized void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			isMouseLeftDown = false;
	}
	public synchronized void mouseWheelMoved(MouseWheelEvent arg0) {
		zoomFactor += arg0.getWheelRotation()*10;
		zoomFactor = Math.max(zoomFactor,0.5f);
	}
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == jspInterpolationQuality)
		{
			jspInterpolationQuality.validate();
			setInterpolation(Integer.parseInt(jspInterpolationQuality.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumXTicks)
		{
			jspNumXTicks.validate();
			setNumTicksX(Integer.parseInt(jspNumXTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumHTicks)
		{
			jspNumHTicks.validate();
			setNumTicksZ(Integer.parseInt(jspNumHTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumZTicks)
		{
			jspNumZTicks.validate();
			setNumTicksH(Integer.parseInt(jspNumZTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorXTicks)
		{
			jspNumMinorXTicks.validate();
			setNumMinorTicksX(Integer.parseInt(jspNumMinorXTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorHTicks)
		{
			jspNumMinorHTicks.validate();
			setNumMinorTicksZ(Integer.parseInt(jspNumMinorHTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jspNumMinorZTicks)
		{
			jspNumMinorZTicks.validate();
			setNumMinorTicksH(Integer.parseInt(jspNumMinorZTicks.getValue().toString()));
		}
		else if (arg0.getSource() == jslAxisFontSize){
			setTextZoom(jslAxisFontSize.getValue()/(float)100);
		}
		else if (arg0.getSource() == jslZScale){
			setZScale((jslZScale.getValue()/(float)100));
		}
	}
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == mitRenderModeFill)
		{
			setCurrentFill(HeightMap3DRenderer.fillOption.FO_FILL);
			mitRenderModeWire.setSelected(false);
			mitRenderModeBoth.setSelected(false);
		}
		else if (arg0.getSource() == mitRenderModeWire)
		{
			setCurrentFill(HeightMap3DRenderer.fillOption.FO_WIRE);
			mitRenderModeFill.setSelected(false);
			mitRenderModeBoth.setSelected(false);
		}
		else if (arg0.getSource() == mitRenderModeBoth)
		{
			setCurrentFill(HeightMap3DRenderer.fillOption.FO_BOTH);
			mitRenderModeFill.setSelected(false);
			mitRenderModeWire.setSelected(false);
		}
		else if (arg0.getSource() == mitSnapshot)
		{
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            paint(graphics2D);
            try{
            	JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "PNG Image", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	ImageIO.write(image,"png", new File(chooser.getSelectedFile().getAbsolutePath()+".png"));
                }
            }
            catch (Exception e){
            	JOptionPane.showMessageDialog(this,
    				    "Unable to save image. Please choose a different location.",
    				    "I/O Error",
    				    JOptionPane.ERROR_MESSAGE);
            }
		}
		
	}


}

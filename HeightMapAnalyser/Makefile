#Makefile for Heightmap Analyser
#(C) Benjamin Hugo

#Setup
JAVACPATH = javac
BINDIR = bin/
SRCDIR = src/
LIBRARIES_DIR = lib/
JARSDIR = lib/
#Declare JARS 
JARS = $(JARSDIR)gluegen-rt.jar:$(JARSDIR)gluegen-rt-natives-linux-amd64.jar:$(JARSDIR)gluegen-rt-natives-windows-amd64.jar:$(JARSDIR)gnujaxp.jar:$(JARSDIR)iText-2.1.5.jar:$(JARSDIR)jcommon-1.0.17.jar:$(JARSDIR)jfreechart-1.0.14-experimental.jar:$(JARSDIR)jfreechart-1.0.14.jar:$(JARSDIR)jfreechart-1.0.14-swt.jar:$(JARSDIR)jogl-all.jar:$(JARSDIR)jogl-all-natives-linux-amd64.jar:$(JARSDIR)jogl-all-natives-windows-amd64.jar:$(JARSDIR)jogl-core.jar:$(JARSDIR)jogl-gldesktop-dbg.jar:$(JARSDIR)jogl-gldesktop.jar:$(JARSDIR)jogl-os-win.jar:$(JARSDIR)jogl-os-x11.jar:$(JARSDIR)junit.jar:$(JARSDIR)nativewindow-core.jar:$(JARSDIR)nativewindow-os-win.jar:$(JARSDIR)nativewindow-os-x11.jar:$(JARSDIR)newt-core.jar:$(JARSDIR)newt-driver-broadcomegl.jar:$(JARSDIR)newt-driver-intelgdl.jar:$(JARSDIR)newt-driver-kd.jar:$(JARSDIR)newt-driver-win.jar:$(JARSDIR)newt-driver-x11.jar:$(JARSDIR)newt-natives-linux-amd64.jar:$(JARSDIR)newt-natives-windows-amd64.jar:$(JARSDIR)newt-ogl.jar:$(JARSDIR)pdfbox-app-1.7.1.jar:$(JARSDIR)servlet.jar:$(JARSDIR)sgt_v30.jar:$(JARSDIR)swtgraphics2d.jar
#Define parameters for java and javac
PARAMS = -g -cp "$(SRCDIR):$(JARS)" -d $(BINDIR)
RUNPARAMS = -cp "$(BINDIR):$(JARS)" -D"java.library.path=$(LIBRARIES_DIR)"
.SUFFIXES: .java .class

#Tell make to look in the source folder
VPATH = $(SRCDIR) $(BINDIR) $(SRCDIR)Drawing3D/ $(SRCDIR)Data/ $(SRCDIR)GraphicsStuff/ $(SRCDIR)Forms/ $(SRCDIR)/HeightMapAnalyser $(SRCDIR)GUITools/

#Compile driver:
.java.class:
	@if [ ! -d $(BINDIR) ]; then mkdir $(BINDIR); fi; $(JAVACPATH) $(PARAMS) $<
runner.class:runner.java MainForm.class
HeightMap.class:HeightMap.java ImageCreator.class
heighMapDisplay.class:heighMapDisplay.java HeightMap.class GridLayoutVerticalMod.class
ImageDrawer.class:ImageDrawer.java DrawingPane.class ActiveMap.class MainForm.class
MainForm.class:MainForm.java GraphContainer.class ComparisonScreen.class SelectPage.class HeightMapList.class ActiveMap.class
DrawingPane.class:DrawingPane.java MainForm.class ActiveMap.class
GraphContainer.class:GraphContainer.java HeightMap.class ActiveMap.class
ActiveMap.class:ActiveMap.java HeightMap.class
ComparisonScreen.class:ComparisonScreen.java HeightMap.class HeightMap3DRenderer.class ActiveMap.class DifferanceMap.class
HeightMapList.class:HeightMapList.java HeightMap.class heighMapDisplay.class
SelectPage.class:SelectPage.java HeightMap.class heighMapDisplay.class GridLayoutVerticalMod.class
DifferanceMap.class:DifferanceMap.java HeightMap.class
HeightMap3DRenderer.class:HeightMap3DRenderer.java SurfaceInterpolator.class HeightMap.class
GridLayoutVerticalMod.class:GridLayoutVerticalMod.java

run:
	clear
	java $(RUNPARAMS) HeightMapAnalyser.runner
crun:
	make clean
	make
	make run
clean:
	@echo Cleaning out all class files. Stand by
	@if [ -d $(BINDIR) ]; then rm -r $(BINDIR); fi
	@echo Done.

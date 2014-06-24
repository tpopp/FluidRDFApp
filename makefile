default:
	jar xf ./lib/jcommon-1.0.21.jar; jar xf ./lib/jfreechart-1.0.17.jar
	mv org/ com/ src/
	cd src; \
	javac -cp "../lib/jcommon-1.0.21.jar;../lib/jfreechart-1.0.17.jar" */*.java; \
	jar cvfm ../FluidInfo.jar ../MANIFEST.MF Applet/* calculations/* */*.dat org/* com/*; \
	rm -rf com/ org/

clean:
	rm -rf src/org src/com

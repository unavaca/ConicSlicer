package com.output;

import java.io.File;
import java.io.IOException;

import com.model.Settings;

/**
 * This calls cura for planar gcode, eventually
 * we will have our own planar slicer.
 */
public class ExternalSlicer {
	public ExternalSlicer(String slicerCmd) {
		// TODO
	}
	
	public File sliceToGCode(File stl, Settings s, File outGcode) throws IOException, InterruptedException{
		// TODO
		return null;
	}
}

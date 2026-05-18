package com.slicing;

import java.io.File;

import com.model.Mesh;
import com.model.Settings;

/**
 * This is the planar slicer made entirely by me. It will be used by the planarly sliced portion of the print.
 * 
 * @author Zach Brinton
 * @version 5-18-2026
 */
public class PlanarSlicer {
	public PlanarSlicer() {
		// No initialization needed.
	}
	
	public static void slice(Mesh mesh, Settings settings, File outputFile) {
		int wallPerimeters = settings.getWallPerimeters();
		float fillPercentage = settings.getFillPercentage();
		
		
		
		
		
	}
}

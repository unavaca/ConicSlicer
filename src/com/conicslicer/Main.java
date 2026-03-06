package com.conicslicer;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Completes the process of generating conic G-code from an STL file.
 * 
 * 
 * <p>Ignore this:</p>@deprecated
 * <ol>
 * <li>Parse the STL: {@link STLParser}</li>
 * <li>Refine the mesh: {@link MeshRefiner}</li>
 * <li>Transform to conic space: {@link ConicMapper}</li>
 * <li>Slice: {@link Slicer}</li>
 * <li>Generate planar G-code: {@link GCodeGenerator}</li>
 * <li>Back-transform to conical G-code: {@link GCodeBackTransformer}</li>
 * <li>Save the result</li>
 * </ol>
 * 
 * <p>New pipeline</p>
 * <ol>
 * <li>Load triangles: {@link STLParser}</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ol>
 * 
 * 
 * <p>Accepts command-line arguments for the STL path, cone center and direction, layer height,
 * extrusion width, and output file names.</p>
 * 
 * @author Zach Brinton
 * @version 3-6-26
 */
public class Main {
	private static final int refineThreshold = 3;
	private static final double filamentDiameter = 1.75d;
	private static final int feedrates = -1;
	private static final int centerX = 0;
	private static final int centerY = 0;
	private static final String resultFilePath = "example.gcode";
	
	public static void main(String[] args) throws IOException {
		// 1) Read args
		File stl = new File(args[0]);
		File out = new File(args[1]);
		
		// TODO make the settings read args.
		// Settings settings = Settings.defaults();
		List<Triangle> mesh = STLParser.parse(stl);
		
		// Choose conic axis center (settings) -- user provided (cx, cy) / default to model center in XY from bounds
		
		// Compute s range
		
		// Prepare writers/planners
		
		// Slice each conic layer
		
		// Stitch segments -> loops
		
		// Toolpaths
		
		// Emit G-code
	}
	
	/* @deprecated */
	public static void deprecatedMain(String[] args) {
		// List<Triangle> mesh = STLParser.parse("example.stl");
		// mesh = MeshRefiner.refine(mesh, refineThreshold);
		// mesh = ConicMapper.transform(mesh);
		// List<ToolPath> slicedMesh = Slicer.slice(mesh);
		// GCodeGenerator.generate(slicedMesh, filamentDiameter, feedrates);
		// GCodeBackTransformer.transform(gCodeFile, centerX, centerY);
		// saveConicalGCode(resultFilePath);
	}
	
	private static void saveConicalGCode(String filePath) {
		throw new UnsupportedOperationException();
		// TODO
	}
}

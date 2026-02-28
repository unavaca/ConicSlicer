package com.conicslicer;

import java.io.File;
import java.util.List;

/**
 * Completes the process of generating conic G-code from an STL file.
 * 
 * <p>Does the following:</p>
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
 * <p>Accepts command-line arguments for the STL path, cone center and direction, layer height,
 * extrusion width, and output file names.</p>
 * 
 * @author Zach Brinton
 * @version 2-20-26
 */
public class Main {
	private static final int refineThreshold = 3;
	private static final double filamentDiameter = 1.75d;
	private static final int feedrates = -1;
	private static final int centerX = 0;
	private static final int centerY = 0;
	private static final String resultFilePath = "example.gcode";
	
	/**
	 * As described by the method header.
	 * 
	 * @param args // TODO explain accepted arguments
	 */
	public static void main(String[] args) {
		List<Triangle> mesh = STLParser.parse("example.stl");
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

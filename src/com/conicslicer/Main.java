package com.conicslicer;

import java.io.File;

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
	/**
	 * As described by the method header.
	 * 
	 * @param args // TODO explain accepted arguments
	 */
	public static void main(String[] args) {
		System.out.println("Hello G-code");
	}
	
	private static void saveConicalGCode(File file) {
		throw new UnsupportedOperationException();
		// TODO
	}
}

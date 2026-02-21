package com.conicslicer;

import java.io.File;
import java.util.List;

/**
 * Reads a binary or ASCII STL file and constructs a list of {@link Triangle} objects.
 * 
 * <p>Each {@link Triangle} stores its three {@link Vertex} points (with x, y, z) and the normal.</p>
 * <p>When parsing an ASCII STL, recognise the facet normal/vertex structure.</p>
 * <p>When parsing a binary STL, read the 80-byte header and 50-byte records per triangle.</p>
 * <p>An example of an ASCII STL and a binary STL can be found in the resources folder.</p>
 * 
 * @author Zach Brinton
 * @version 2-20-26
 */
public class STLParser {
	/**
	 * This method takes an STL file and returns the list of all triangles in that STL.
	 * 
	 * @param file an STL file representing the model to be sliced.
	 * @return a list of all the triangle objects found in the given STL file.
	 */
	public static List<Triangle> parse(File file) {
		throw new UnsupportedOperationException();
		// TODO
	}
	
	private static boolean isAscii(File file) {
		throw new UnsupportedOperationException();
		// TODO
	}
}

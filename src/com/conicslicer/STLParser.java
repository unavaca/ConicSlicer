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
 */
public class STLParser {
	/**
	 * This method takes an STL file and returns the list of all triangles in that STL.
	 * @param file: an STL file representing a model to be sliced.
	 * @return List<Triangle>: a list of all the triangle objects found in that STL file.
	 */
	public List<Triangle> parse(File file) {
		throw new UnsupportedOperationException();
	}
	
	private boolean isAscii() {
		throw new UnsupportedOperationException();
		
	}
}

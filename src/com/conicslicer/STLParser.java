package com.conicslicer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private String _filePath;
	private List<Triangle> _mesh;
	
	/**
	 * Sets the file and 
	 * @param file
	 */
	public STLParser(String filePath) {
		_filePath = filePath;
		
		_mesh = isAscii() ? parseAscii() : parseBinary();
	}
	
	public List<Triangle> getMesh() {
		return _mesh;
	}
	
	/**
	 * This method takes an STL file and returns the list of all triangles in that STL.
	 * 
	 * @param file an STL file representing the model to be sliced.
	 */
	private List<Triangle> parseAscii() {		
		try {
			long linesCount = Files.lines(Paths.get(_filePath)).count();
		} catch (IOException e) {
			System.err.println("IOException: file path \"" + _filePath + "\" not found\nError message: " + e.getMessage());
		}
		
		
		
		return null;
	}
	
	private List<Triangle> parseBinary() {
		// TODO
		return null;
	}
	
	private boolean isAscii() {
		// TODO
		return true;
	}
}

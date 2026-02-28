package com.conicslicer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
 * @version 2-27-26
 */
public class STLParser {
	/**
	 * Sets the file and 
	 * @param file
	 */
	public static List<Triangle> parse(String filePath) {
		return isAscii() ? parseAscii(filePath) : parseBinary(filePath);
	}
	
	/**
	 * This method takes an STL file and returns the list of all triangles in that STL.
	 * 
	 * @param file an STL file representing the model to be sliced.
	 */
	private static List<Triangle> parseAscii(String filePath) {		
		try {
			long linesCount = Files.lines(Paths.get(filePath)).count();
		} catch (IOException e) {
			System.err.println("IOException: file path \"" + filePath + "\" not found\nError message: " + e.getMessage());
		}
		
		List<Triangle> mesh = new ArrayList<>();
		
		
		return mesh;
	}
	
	private static List<Triangle> parseBinary(String filePath) {
		// TODO
		return null;
	}
	
	private static boolean isAscii() {
		// TODO
		return true;
	}
}

package com.conicslicer;

/**
 * Provides the conic space mapping functions.
 * 
 * <p>Fields include the rotation center {@code (c_x, c_y)} and a boolean indicating 
 * inverse mapping (pre-deformation) or direct mapping (back-transform).</p>
 * 
 * @author Zach Brinton
 * @version 2-20-26
 */
public class ConicMapper {
	
	/**
	 * Lifts each vertex along z by its radial distance from the cone axis; records rotation angle.
	 * TODO
	 * @param p TODO
	 * @return TODO
	 */
	public Vertex inverseMap(Vertex p) {
		throw new UnsupportedOperationException();
		// TODO
	}
	
	/**
	 * Given a deformed vertex {@code (x, y, z')}, computes the original height {@code z = z' - distance(x,y)} and the rotation angle.
	 * This is used when back-transforming G-code.
	 * 
	 * @param p the deformed vertex (x, y, z') // TODO explain z prime.
	 * @return // TODO
	 */
	private Vertex directMap(Vertex p) {
		throw new UnsupportedOperationException();
		// TODO
	}
}

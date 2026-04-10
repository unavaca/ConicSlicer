package com.geometry;

/**
 * Represents a line segment between two vertices.
 * 
 * <p>This is used to represent the intersection of a triangle with a horizontal plane, 
 * which can be either a single line segment or two coincident line segments in the case 
 * of a degenerate triangle.</p>
 * 
 * @author Zach Brinton
 * @version 4-10-26
 */
public final class Segment {
	public final Vertex v1;
	public final Vertex v2;
	
	public Segment(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
}

package com.geometry;

import java.util.Optional;

/**
 * Represents one triangular facet from an STL mesh.
 * 
 * <p>An STL facet stores:
 * <ul>
 *   <li>a normal vector (nx, ny, nz)</li>
 *   <li>three vertices (v1, v2, v3)</li>
 * </ul>
 * </p>
 *
 * <p>Note: the normal stored in an STL file is not reliable! I have come aware 
 * that some programs recompute it from the vertices instead.</p>
 * <p>Also note: I use the term {@code degenerate triangle} in this documentation
 * to describe triangles that are collinear, in other words, that are completely
 * flat appearing as a line segment rather than a true triangle. Such triangles
 * have an area of zero.</p>
 * 
 * @author Zach Brinton and GPT 5.2
 * @version 3-6-26
 */
public final class Triangle {
	public final Vertex normal;
	
	public final Vertex v1;
	public final Vertex v2;
	public final Vertex v3;
	
	public Triangle(Vertex normal, Vertex v1, Vertex v2, Vertex v3) {
	    if (normal == null || v1 == null || v2 == null || v3 == null) {
	        throw new IllegalArgumentException("normal and vertices must be non-null");
	    }
	    this.normal = normal;
	    this.v1 = v1;
	    this.v2 = v2;
	    this.v3 = v3;
	}
    
	@Override
	public String toString() {
		return "Triangle(normal=" + normal + ", v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + ")";
	}
	
    /**
     * Checks exact equality of two triangles (exact float-bit equality through Vertex.equals).
     *
     * <p>Note: We might have to introduce a tolerance in the future because sometimes there
     * are very slight discrepancies in STL files and the exact equality is too strict.</p>
     *
     * @param o another object
     * @return true if all fields are exactly equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triangle t)) return false;
        return v1.equals(t.v1) && v2.equals(t.v2) && v3.equals(t.v3);
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + v1.hashCode();
        h = 31 * h + v2.hashCode();
        h = 31 * h + v3.hashCode();
        return h;
    }
}

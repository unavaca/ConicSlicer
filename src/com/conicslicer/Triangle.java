package com.conicslicer;

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
 * @author Zach Brinton
 * @version 3-5-26
 */
public final class Triangle {
	public final Vertex normal;
	
	public final Vertex v1;
	public final Vertex v2;
	public final Vertex v3;
	
	public Triangle(Vertex normal, Vertex v1, Vertex v2, Vertex v3) {
		this.normal = normal;
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	/**
	 * Computes the geometric normal from the vertices using the right-hand rule.
	 * 
     * <p>Steps:
     * <ol>
     *   <li>Compute two edges: e1 = v2 - v1 and e2 = v3 - v1</li>
     *   <li>Cross product: n = e1 x e2 (perpendicular to the triangle)</li>
     *   <li>Normalize: n / |n| to make it unit length</li>
     * </ol>
     * </p>
     *
     * <p>If the vertices are collinear (degenerate triangle), the cross product is (0,0,0),
     * so the returned normal will be (0,0,0).</p>
     *
     * @return the computed unit normal (or (0,0,0) for degenerate triangles)
	 */
	public Vertex computedNormal() {
		Vertex e1 = v2.subtract(v1);
		Vertex e2 = v3.subtract(v1);
		return e1.cross(e2).normalized();
	}
	
	/**
	 * Computes the area of this triangle.
	 * 
	 * <p>The area is half the length of the cross product of two edge vectors:
	 * {@code area = 0.5 * |(v2 - v1) x (v3 - v1)|}.</p>
	 * 
	 * @return triangle area (0 for degenerate triangles)
	 */
	public float area() {
		Vertex e1 = v2.subtract(v1);
		Vertex e2 = v3.subtract(v1);
		return 0.5f * e1.cross(e2).length();
	}
	
	/**
	 * The centroid of a triangle is the average of its three vertices.
	 * 
	 * <p>This is the "center point" of the triangle. It is also calculated
	 * by drawing a perpendicular line from the middle of each edge in a
	 * triangle, and seeing where they intersect.</p>
	 * 
	 * @return centroid point
	 */
	public Vertex centroid() {
		return new Vertex(
			(v1.x + v2.x + v3.x) / 3f,
			(v1.y + v2.y + v3.y) / 3f,
			(v1.z + v2.z + v3.z) / 3f 
		);
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
        return normal.equals(t.normal) && v1.equals(t.v1) && v2.equals(t.v2) && v3.equals(t.v3);
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + normal.hashCode();
        h = 31 * h + v1.hashCode();
        h = 31 * h + v2.hashCode();
        h = 31 * h + v3.hashCode();
        return h;
    }
    
    /**
     * Computes the intersection between this triangle and the implicit surface f(p)=0.
     *
     * <p>Most of the time, the intersection is either empty or a single line segment.
     * This method returns the segment if it finds two distinct intersection points.</p>
     *
     * <p>Degenerate cases (the surface lies along a whole edge, or the intersection collapses
     * to a single point) return empty because they are ambiguous for building contours.</p>
     *
     * @param field scalar field defining the surface (surface is where field.eval(p) == 0)
     * @param eps tolerance for considering a point "on the surface" (e.g. 1e-6f to 1e-4f)
     * @return Optional containing a segment, or empty if no usable segment
     */
    public Optional<Segment3D> intersectIsosurface(ScalarField field, float eps) {
        // Evaluate f at vertices
        float f1 = field.eval(v1);
        float f2 = field.eval(v2);
        float f3 = field.eval(v3);

        // Quick reject: all strictly positive or all strictly negative (with eps margin)
        boolean p1 = f1 > eps, n1 = f1 < -eps;
        boolean p2 = f2 > eps, n2 = f2 < -eps;
        boolean p3 = f3 > eps, n3 = f3 < -eps;

        if ((p1 && p2 && p3) || (n1 && n2 && n3)) {
            return Optional.empty();
        }

        // Collect up to two distinct intersection points from edges
        Vertex[] pts = new Vertex[2];
        int count = 0;

        count = addEdgeIntersection(v1, f1, v2, f2, eps, pts, count);
        if (count > 2) return Optional.empty();

        count = addEdgeIntersection(v2, f2, v3, f3, eps, pts, count);
        if (count > 2) return Optional.empty();

        count = addEdgeIntersection(v3, f3, v1, f1, eps, pts, count);
        if (count > 2) return Optional.empty();

        if (count == 2) {
            // If the two points are basically the same, treat as point -> ignore
            if (pts[0].subtract(pts[1]).length() <= eps) {
                return Optional.empty();
            }
            return Optional.of(new Segment3D(pts[0], pts[1]));
        }

        return Optional.empty();
    }

    /**
     * Adds the intersection point between an edge (a->b) and the isosurface f=0, if it exists.
     *
     * <p>We use linear interpolation along the edge:
     * If fa and fb have opposite signs, then crossing occurs at:
     *
     * <pre>
     * t = fa / (fa - fb)
     * p = a + t*(b-a)
     * </pre>
     *
     * <p>We also handle endpoint-on-surface cases (|f| <= eps).</p>
     *
     * @return updated count, or count+1 if it would exceed 2 distinct points
     */
    private static int addEdgeIntersection(Vertex a, float fa, Vertex b, float fb, float eps, Vertex[] out, int count) {
        boolean aOn = Math.abs(fa) <= eps;
        boolean bOn = Math.abs(fb) <= eps;

        // Whole edge lies on surface (degenerate) -> skip (ambiguous)
        if (aOn && bOn) {
            return count;
        }

        Vertex p = null;

        if (aOn) {
            p = a;
        } else if (bOn) {
            p = b;
        } else {
            // Proper sign change => crossing
            if ((fa > 0f && fb < 0f) || (fa < 0f && fb > 0f)) {
                float t = fa / (fa - fb);
                float x = a.x + t * (b.x - a.x);
                float y = a.y + t * (b.y - a.y);
                float z = a.z + t * (b.z - a.z);
                p = new Vertex(x, y, z);
            }
        }

        if (p == null) return count;

        // Deduplicate
        for (int i = 0; i < count; i++) {
            if (out[i].subtract(p).length() <= eps) {
                return count;
            }
        }

        if (count < 2) {
            out[count] = p;
            return count + 1;
        }

        // Too many distinct points (degenerate situation)
        return count + 1;
    }

    /**
     * A line segment in 3D. For slicing, these segments will chain together into loops.
     */
    public static final class Segment3D {
        public final Vertex a;
        public final Vertex b;

        public Segment3D(Vertex a, Vertex b) {
            if (a == null || b == null) throw new IllegalArgumentException("endpoints must be non-null");
            this.a = a;
            this.b = b;
        }
    }
}

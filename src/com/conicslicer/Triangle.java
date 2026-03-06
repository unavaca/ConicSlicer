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
     * <p>This method examines a single triangle edge with endpoints {@code a} and {@code b}.
     * The scalar field values at those endpoints are {@code fa = f(a)} and {@code fb = f(b)}.</p>
     *
     * <p>Cases handled:</p>
     * <ul>
     *   <li>If both endpoints are “on” the surface (|f| <= eps), the whole edge lies on the surface
     *       (degenerate/ambiguous), so nothing is added.</li>
     *   <li>If exactly one endpoint is on the surface, that endpoint is used as the intersection point.</li>
     *   <li>If {@code fa} and {@code fb} have opposite signs, the surface crosses the edge at one point.
     *       We find it by linear interpolation.</li>
     * </ul>
     *
     * <p>Interpolation math (when crossing occurs):</p>
     * <pre>
     * t = fa / (fa - fb)
     * p = a + t * (b - a)
     * </pre>
     *
     * <p>We also deduplicate points: if the new intersection point is within {@code eps} of an existing
     * point already stored in {@code out}, we do not add it again.</p>
     *
     * @param a one endpoint of the edge
     * @param fa scalar field value at {@code a} (fa = f(a))
     * @param b the other endpoint of the edge
     * @param fb scalar field value at {@code b} (fb = f(b))
     * @param eps tolerance used for:
     *            (1) deciding if a point is “on” the surface (|f| <= eps),
     *            (2) deduplicating points (distance <= eps),
     *            (3) guarding against unstable interpolation when |fa - fb| is very small
     * @param out an array that stores up to 2 distinct intersection points found so far
     * @param count how many points in {@code out} are currently valid (0, 1, or 2)
     * @return the updated count. If it returns {@code count + 1} when {@code count} was already 2,
     *         that signals “too many distinct points” (a degenerate situation for slicing).
     */
    private static int addEdgeIntersection(
        Vertex a, float fa,
        Vertex b, float fb,
        float eps,
        Vertex[] out, int count
    ) {
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
                float denom = fa - fb;
                if (Math.abs(denom) <= eps) {
                    // Interpolation would be unstable; treat as no intersection on this edge.
                    return count;
                }

                float t = fa / denom;
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

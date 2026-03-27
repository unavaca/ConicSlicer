package com.geometry;

/**
 * Represents a single vertex of dimension three.
 * 
 * <p>Coordinates are shown as the three floats x, y, and z.</p>
 * <p>Note the interchanging use of the term vector to describe this vertex.</p>
 * 
 * @author Zach Brinton
 * @version 3-5-26
 */
public final class Vertex {
	public float x;
	public float y;
	public float z;
	
	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vertex add(Vertex other) {
		return new Vertex(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Vertex subtract(Vertex other) {
		return new Vertex(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Vertex scale(float s) {
		return new Vertex(this.x * s, this.y * s, this.z * s);
	}
	
    /**
     * Computes the dot product of this vector with another.
     *
     * <p>Note:
     * <ul>
     *   <li>{@code dot(a,b) = |a||b|cos(theta)}</li>
     *   <li>If dot == 0, the vectors are perpendicular (90°) (ignoring floating error).</li>
     *   <li>Dot is used a lot for angles, projections, and checking orientation.</li>
     * </ul>
     * </p>
     * 
     * @param other the other vector
     * @return the dot product (a scalar)
     */ 
	public float dot(Vertex other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	/**
	 * Computes the cross product of this vector with another.
	 * 
	 * <p>The result is a vector perpendicular to both inputs, with direction given by the
	 * right-hand rule. The length of the result equals {@code |a||b|sin(θ)}.</p>
	 * 
	 * <p>This is how you compute a triangle's normal from its edges.</p>
	 * 
	 * @param other the other vector
	 * @return the cross product (a vector)
	 */
	public Vertex cross(Vertex other) {
		return new Vertex(
			this.y * other.z - this.z * other.y,
			this.z * other.x - this.x * other.z,
			this.x * other.y - this.y * other.x
		);
	}
	
	/**
	 * Computes the length (magnitude) of this vector.
	 * 
	 * <p>Length is {@code sqrt(x^2 + y^2 + z^z)}.</p>
	 * 
	 * @return the vector length
	 */
	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	/**
	 * Returns a a unit-length version of this vector (same direction, length 1)
	 * 
	 * <p>If this vector has length 0 (all components 0), there is no direction to normalize</p>
	 * @return a normalized (unit) vector, or (0, 0, 0) if length is 0
	 */
	public Vertex normalized() {
		float len = length();
		if (len == 0f) return new Vertex(0f, 0f, 0f);
		return scale(1f / len);
	}
	
	@Override
	public String toString() {
		return "Vertex(" + x + ", " + y + ", " + z + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vertex v)) return false;
		return Float.floatToIntBits(x) == Float.floatToIntBits(v.x)
			&& Float.floatToIntBits(y) == Float.floatToIntBits(v.y)
			&& Float.floatToIntBits(z) == Float.floatToIntBits(v.z);
	}
	
	@Override
	public int hashCode() {
		int hx = Float.floatToIntBits(x);
		int hy = Float.floatToIntBits(y);
		int hz = Float.floatToIntBits(z);
		int h = 17;
		h = 31 * h + hx;
		h = 31 * h + hy;
		h = 31 * h + hz;
		return h;
	}
}

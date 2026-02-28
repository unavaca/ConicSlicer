package com.conicslicer;

/**
 * Represents a single vertex of dimension three.
 * 
 * <p>Coordinates are shown as the three floats x, y, and z.</p>
 * 
 * @author Zach Brinton
 * @version 2-27-26
 */
public class Vertex {
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
	
	public float dot(Vertex other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	public Vertex cross(Vertex other) {
		return new Vertex(
			this.y * other.z - this.z * other.y,
			this.z * other.x - this.x * other.z,
			this.x * other.y - this.y * other.x
		);
	}
	
	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
}

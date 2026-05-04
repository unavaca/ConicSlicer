package com.model;

import java.util.Iterator;
import java.util.List;

import com.geometry.Triangle;
import com.geometry.Vertex;

/**
 * Wrapper class for the list of triangles we use to represent a mesh.
 * 
 * <p>This class is iterable, and designed to be used in for-each loops and the like.</p>
 * 
 * @version 4-1-26
 * @author Zach Brinton
 */
public class Mesh implements Iterable<Triangle> {
	private List<Triangle> _mesh;
	private Vertex _center;
	
	public Mesh(List<Triangle> mesh) {
		_mesh = mesh;
	}
	
	public boolean add(Triangle triangle) {
		return _mesh.add(triangle);
	}
	
	public List<Triangle> triangles() {
		return _mesh;
	}
	
	public int size() {
		return _mesh.size();
	}
	
	public Vertex center() {
		if (_center == null) {
			float xSum = 0.0f;
			float ySum = 0.0f;
			float zSum = 0.0f;
			int vertexCount = 0;
			
			for (var triangle : _mesh) {
				xSum += triangle.v1.x + triangle.v2.x + triangle.v3.x;
				ySum += triangle.v1.y + triangle.v2.y + triangle.v3.y;
				zSum += triangle.v1.z + triangle.v2.z + triangle.v3.z;
				vertexCount += 3;
			}
			
			_center = new Vertex(xSum / vertexCount, ySum / vertexCount, zSum / vertexCount);
		}
		
		return _center;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return _mesh.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (var triangle : _mesh) {
			sb.append(triangle.toString()).append("\n");
		}
		return sb.toString();
	}
}

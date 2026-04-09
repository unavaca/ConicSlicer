package com.model;

import java.util.Iterator;
import java.util.List;

import com.geometry.Triangle;

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

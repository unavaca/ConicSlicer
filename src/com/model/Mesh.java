package com.model;

import java.util.Iterator;
import java.util.List;

import com.geometry.Triangle;

/**
 * Wrapper class for the list of triangles we use to represent a mesh.
 * 
 * @version 4-1-26
 * @author Zach Brinton
 */
public class Mesh implements Iterable<Triangle> {
	private List<Triangle> _mesh;
	
	public Mesh(List<Triangle> mesh) {
		_mesh = mesh;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return _mesh.iterator();
	}
}

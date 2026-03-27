package com.model;

import java.util.List;

import com.geometry.Bounds;
import com.geometry.Triangle;

/**
 * Wrapper class for the list of triangles we use to represent a mesh.
 * 
 * @version 3-6-26
 * @author Zach Brinton
 */
public class Mesh {
	private List<Triangle> _mesh;
	
	public Mesh(List<Triangle> mesh) {
		_mesh = mesh;
	}
	
	public List<Triangle> triangles() {
		return _mesh;
	}
	
	public Bounds bounds() {
		// TODO
		return null;
	}
	
	
}

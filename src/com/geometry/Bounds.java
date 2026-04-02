package com.geometry;

import com.model.Mesh;

/**
 * Represents the axis-aligned bounding box of a mesh.
 * 
 * <p>A bounding box stores the minimum and maximum x, y, and z coordinates found in the mesh.
 * This is useful for finding the size and center of a model.</p>
 * 
 * @author Zach Brinton
 * @version 4-1-26
 */
public final class Bounds {
	public float minX;
	public float maxX;
	public float minY;
	public float maxY;
	public float minZ;
	public float maxZ;
	
	public Bounds(Mesh mesh) {
		
		// Set each bound as the lowest it could possibly be.
		minX = Float.MAX_VALUE;
        maxX = -Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        maxY = -Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxZ = -Float.MAX_VALUE;
        
        for (Triangle triangle : mesh) {
        	Vertex[] vertices = { triangle.v1, triangle.v2, triangle.v3 };
        	
        	for (Vertex vertex : vertices) {
        		if (vertex.x < minX) minX = vertex.x;
        		if (vertex.y < minY) minY = vertex.y;
        		if (vertex.z < minZ) minZ = vertex.z;
        		
        		if (vertex.x > maxX) maxX = vertex.x;
        		if (vertex.y > maxY) maxY = vertex.y;
        		if (vertex.z > maxZ) maxZ = vertex.z;
        	}
        }
	}
	
	public float getCenterX() {
		return (minX + maxX) / 2f;
	}
	
	public float getCenterY() {
		return (minY + maxY) / 2f;
	}
	
	public float getCenterZ() {
		return (minZ + maxZ) / 2f;
	}
	
	public float getWidth() {
		return maxX - minX;
	}
}

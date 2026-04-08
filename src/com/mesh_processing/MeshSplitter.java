package com.mesh_processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.geometry.Triangle;
import com.geometry.Vertex;
import com.model.Mesh;

/**
 * This is a wrapper class designed to split a given mesh and hold its two halves split along a zSplit index.
 * 
 * @version 4-2-26
 * @author Zach Brinton
 */
public class MeshSplitter {
	List<Triangle> lowerMesh;
	List<Triangle> upperMesh;
	
	/**
	 * Creates a splitter that partitions the triangles of a mesh into upper and lower
	 * portions relative to a horizontal split plane at {@code zSplit}.
	 *
	 * <p>Each triangle in {@code mesh} is examined and classified based on the z-values
	 * of its vertices. The {@code eps} tolerance is used when comparing vertex z-values
	 * to the split plane so triangles very close to the boundary are handled consistently
	 * despite floating-point error.</p>
	 *
	 * <p>This constructor processes the input mesh immediately and builds the internal
	 * upper and lower mesh results.</p>
	 *
	 * @param mesh the source mesh to split
	 * @param zSplit the z-coordinate of the horizontal plane used to divide the mesh
	 * @param eps the comparison tolerance used to account for floating-point precision
	 *            when testing whether points lie above, below, or on the split plane
	 */
	public MeshSplitter(Mesh mesh, float zSplit, float eps) {
		Vertex[] verts;
		lowerMesh = new ArrayList<>();
		upperMesh = new ArrayList<>();
		
		for (var triangle : mesh) {
			verts = new Vertex[3];
			
			verts[0] = triangle.v1;
			verts[1] = triangle.v2;
			verts[2] = triangle.v3;
			
			// Sort the vertices by their z values, low to high.
			Arrays.sort(verts);
			
			// If the lowest vertex is above the split plane, the entire triangle is above.
			if (verts[0].z >= zSplit - eps) {
				upperMesh.add(triangle);
				continue;
			}
			
			// If the highest vertex is below the split plane, the entire triangle is below.
			if (verts[2].z < zSplit - eps) {
				lowerMesh.add(triangle);
				continue;
			}
			
			// Otherwise, the triangle is split by the plane in some way.
			
			// placeholder:
			upperMesh.add(triangle);
			
			
			/*
			float zRatio = zSplit - verts.get(0).z / verts.get(2).z - verts.get(0).z;
			float newX = verts.get(0).x + zRatio * (verts.get(2).x - verts.get(0).x);
			float newY = verts.get(0).y + zRatio * (verts.get(2).y - verts.get(0).y);
			Vertex v4 = new Vertex(newX, newY, zSplit);
			
			zRatio = zSplit - verts.get(0).z / verts.get(1).z - verts.get(0).z;
			newX = verts.get(0).x + zRatio * (verts.get(1).x - verts.get(0).x);
			newY = verts.get(0).y + zRatio * (verts.get(1).y - verts.get(0).y);
			Vertex v5 = new Vertex(newX, newY, zSplit);
			
			lowerMesh.add(new Triangle(triangle.normal, verts.get(0), verts.get(1), v5));
			upperMesh.add(new Triangle(triangle.normal,verts.get(1), verts.get(2), v4));
			
			*/
			
		}
	}
	
	public Mesh getLowerMesh() {
		return new Mesh(upperMesh);
	}
	
	public Mesh getUpperMesh() {
		return new Mesh(upperMesh);
	}
}

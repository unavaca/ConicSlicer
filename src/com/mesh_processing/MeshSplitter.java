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
			Vertex v1 = triangle.v1;
			Vertex v2 = triangle.v2;
			Vertex v3 = triangle.v3;
			
			boolean b1 = v1.z < zSplit - eps;
			boolean b2 = v2.z < zSplit - eps;
			boolean b3 = v3.z < zSplit - eps;
			
			int belowCount = 0;
			
			if (b1) belowCount++;
			if (b2) belowCount++;
			if (b3) belowCount++;
			
			// All vertices above:
			if (belowCount == 0) {
				upperMesh.add(triangle);
				continue;
			}
			
			// All vertices below:
			if (belowCount == 3) {
				lowerMesh.add(triangle);
				continue;
			}
			
			// One below, two above:
			if (belowCount == 1) {
				Vertex below, above1, above2;
				
				if (b1) {
					below = v1;
					above1 = v2;
					above2 = v3;
				} else if (b2) {
					below = v2;
					above1 = v3;
					above2 = v1;
				} else {
					below = v3;
					above1 = v1;
					above2 = v2;
				}
				
				Vertex p1 = intersectAtZ(below, above1, zSplit);
				Vertex p2 = intersectAtZ(below, above2, zSplit);
				
				// Lower piece: one triangle
				lowerMesh.add(makeTriangle(below, p1, p2));
				
				upperMesh.add(makeTriangle(above1, above2, p2));
				upperMesh.add(makeTriangle(above1, p2, p1));
			} else {
				// belowCount == 2
				Vertex above, below1, below2;
				
				if (!b1) {
					above = v1;
					below1 = v2;
					below2 = v3;
				} else if (!b2) {
					above = v2;
					below1 = v3;
					below2 = v1;
				} else {
					above = v3;
					below1 = v1;
					below2 = v2;
				}
				
				Vertex p1 = intersectAtZ(above, below1, zSplit);
				Vertex p2 = intersectAtZ(above, below2, zSplit);
				
				upperMesh.add(makeTriangle(above, p1, p2));
				
				lowerMesh.add(makeTriangle(below1, below2, p2));
				lowerMesh.add(makeTriangle(below1, p2, p1));
			}
		}
	}
	
	public Mesh getLowerMesh() {
		return new Mesh(lowerMesh);
	}
	
	public Mesh getUpperMesh() {
		return new Mesh(upperMesh);
	}
	
	private Vertex intersectAtZ(Vertex a, Vertex b, float zSplit) {
	    float t = (zSplit - a.z) / (b.z - a.z);

	    float x = a.x + t * (b.x - a.x);
	    float y = a.y + t * (b.y - a.y);
	    float z = zSplit;

	    return new Vertex(x, y, z);
	}
	
	private Triangle makeTriangle(Vertex a, Vertex b, Vertex c) {
	    Vertex normal = computeNormal(a, b, c);
	    return new Triangle(normal, a, b, c);
	}

	private Vertex computeNormal(Vertex a, Vertex b, Vertex c) {
	    float ux = b.x - a.x;
	    float uy = b.y - a.y;
	    float uz = b.z - a.z;

	    float vx = c.x - a.x;
	    float vy = c.y - a.y;
	    float vz = c.z - a.z;

	    float nx = uy * vz - uz * vy;
	    float ny = uz * vx - ux * vz;
	    float nz = ux * vy - uy * vx;

	    float len = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);

	    if (len == 0f) {
	        return new Vertex(0f, 0f, 0f);
	    }

	    return new Vertex(nx / len, ny / len, nz / len);
	}
}

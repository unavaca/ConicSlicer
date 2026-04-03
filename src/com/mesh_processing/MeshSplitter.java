package com.mesh_processing;

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
		// Iterate over the triangles in the mesh and add them to the upper or lower mesh based on their z-values.
		// Ignore the y and x values of the vertices.
		for (var triangle : mesh) {
			boolean[] zAbove = new boolean[] {
				triangle.v1.z >= zSplit - eps,
				triangle.v2.z >= zSplit - eps,
				triangle.v3.z >= zSplit - eps
			};
			
			// If every vertex is above the split plane, add the triangle to the upper mesh.
			if (zAbove[0] && zAbove[1] && zAbove[2]) {
				upperMesh.add(triangle);
				return;
			}
			
			// If every vertex is below the split plane, add the triangle to the lower mesh.
			if (!zAbove[0] && !zAbove[1] && !zAbove[2]) {
				lowerMesh.add(triangle);
				return;
			}
			
			// If either of these cases have not been hit yet, we know the triangle is split by the plane.
			
			// There are two possibilites:
			// 		- One vertex is above and two are below, which results in a single triangle above and a single trapezoid below.
			// 		- Two vertices are above and one is below, which results in a single triangle below and a single trapezoid above.
			
			boolean moreAbove = (zAbove[0] == zAbove[1]) ? zAbove[0] : zAbove[2];
			
			float x, y, z;
			
			Vertex v1, v2 = null, v3, v4, v5; // These will be the new vertices created by splitting the triangle.
			
			
			// The following complicated code assigns v1 to the highest vertex, and v2 and v3 to the other two vertices.
			if (zAbove[0]) {
				v1 = triangle.v1;
			} else {
				v2 = triangle.v1;
			}
			
			if (zAbove[1]) {
				v1 = triangle.v2;
			} else {
				v3 = triangle.v2;
			}
			
			if (zAbove[2]) {
				v1 = triangle.v3;
			} else {
				if (v2 == null) {
					v2 = triangle.v3;
				} else {
					v3 = triangle.v3;
				}
			}
			
			
			
			// Case 1, more below:
			if (!moreAbove) {
				// Calculate for above triangle.
				
				// v1 should be the highest z value.
				v1 = zAbove[0] ? triangle.v1 : (zAbove[1] ? triangle.v2 : triangle.v3);
				
				
				
				
				
				
				
				// Calculate for above trapezoid.
				
				
				
				
				
			}
			
			
			if (moreAbove) {
				float x1;
				float y1;
				float z1;
			}
			
			
			
			
			
			// If some vertices are above and some below, we need to split the triangle along zSplit, which results into two trapezoids.
			
			
			// How to deal with the two trapezoids:

			
			// Note that a triangle can have some vertices 
			// above the split plane and some below, so we need to check each vertex separately.
			
			// When a triangle has vertices on both sides of the split plane, 
			// we will need to split the triangle into two or three new triangles 
			// that fit entirely above or below the plane. This is a more complex 
			// case that requires additional logic to compute the intersection points 
			// of the triangle edges with the split plane and create new triangles accordingly.
			
			// There will be trapezoid shapes resulting from the split, which we can triangulate into two triangles each.
			// Which way to slice the trapezoid will be determined by whichever diagonal is shorter, to avoid skinny triangles.
			
			
			
			
			
			
			
		}
	}
	
	public Mesh getLowerMesh() {
		return new Mesh(upperMesh);
	}
	
	public Mesh getUpperMesh() {
		return new Mesh(upperMesh);
	}
}

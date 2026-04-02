package com.mesh_processing;

import com.model.Mesh;

/**
 * This is a wrapper class designed to split a given mesh and hold its two halves split along a zSplit index.
 * 
 * @version 4-2-26
 * @author Zach Brinton
 */
public class MeshSplitter {
	Mesh lowerMesh;
	Mesh upperMesh;
	
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
			float[] zValues = {
				triangle.v1.z,
				triangle.v2.z,
				triangle.v3.z
			};
			
			// Check each vertex.
			for (float zValue : zValues) {
				// If every vertex is above the split plane, add the triangle to the upper mesh.
				
				// If every vertex is below the split plane, add the triangle to the lower mesh.
				
				// If some vertices are above and some below, we need to split the triangle along zSplit, which results into two trapezoids.
				
			}
			
			// Note that a triangle can have some vertices 
			// above the split plane and some below, so we need to check each vertex separately.
			
			// When a triangle has vertices on both sides of the split plane, 
			// we will need to split the triangle into two or three new triangles 
			// that fit entirely above or below the plane. This is a more complex 
			// case that requires additional logic to compute the intersection points 
			// of the triangle edges with the split plane and create new triangles accordingly.
			
			// There will be trapezoid shapes resulting from the split, which we can triangulate into two triangles each.
			// Which way to slice the trapezoid will be determined by whichever diagonal is shorter, to avoid skinny triangles.
			
			
			
			
			boolean onOrAbove = vertexZ >= zSplit - eps;
			
			
		}
	}
	
	public Mesh getLowerMesh() {
		return upperMesh;
	}
	
	public Mesh getUpperMesh() {
		return upperMesh;
	}
}

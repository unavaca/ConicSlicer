package com.mesh_processing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.geometry.Segment;
import com.geometry.Triangle;
import com.geometry.Vertex;
import com.model.Mesh;

/**
 * This is a wrapper class designed to split a given mesh and hold its two halves split along a zSplit index.
 * 
 * @version 5-4-26
 * @author Zach Brinton
 */
public class MeshSplitter {
	private List<Triangle> lowerMesh;
	private List<Triangle> upperMesh;
	private HashSet<Segment> segments;
	private List<List<Vertex>> edgeLoops;
	
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
		lowerMesh = new ArrayList<>();
		upperMesh = new ArrayList<>();
		segments = new HashSet<>();
		
		for (var triangle : mesh) {
			Vertex v1 = triangle.v1;
			Vertex v2 = triangle.v2;
			Vertex v3 = triangle.v3;
			
			boolean v1IsBelow = v1.z < zSplit - eps;
			boolean v2IsBelow = v2.z < zSplit - eps;
			boolean v3IsBelow = v3.z < zSplit - eps;
			
			int belowCount = 0;
			
			if (v1IsBelow) belowCount++;
			if (v2IsBelow) belowCount++;
			if (v3IsBelow) belowCount++;
			
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
				
				if (v1IsBelow) {
					below = v1;
					above1 = v2;
					above2 = v3;
				} else if (v2IsBelow) {
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
				
				Segment s = new Segment(p1, p2);
				segments.add(s);
				
				lowerMesh.add(makeTriangle(below, p1, p2));
				
				upperMesh.add(makeTriangle(above1, above2, p2));
				upperMesh.add(makeTriangle(above1, p2, p1));
			} else {
				// Two below, one above:
				Vertex above, below1, below2;
				
				if (!v1IsBelow) {
					above = v1;
					below1 = v2;
					below2 = v3;
				} else if (!v2IsBelow) {
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
				
				Segment s = new Segment(p1, p2);
				segments.add(s);
				
				upperMesh.add(makeTriangle(above, p1, p2));
				
				lowerMesh.add(makeTriangle(below1, below2, p2));
				lowerMesh.add(makeTriangle(below1, p2, p1));
			}
			
			edgeLoops = buildLoops(segments, eps);
			
			
			for (List<Vertex> loop : edgeLoops) {
				System.out.println("Loop:");
				for (Vertex v : loop) {
					System.out.println(v);
				}
			}
			
//			for (List<Vertex> loop : edgeLoops) {
//				Vertex center = findLoopCenter(loop);
//				for (Vertex v : loop) {
//					upperMesh.add(new Triangle(new Vertex(0f, 0f, -1f), new Vertex(center.x, center.y, zSplit), v, loop.get((loop.indexOf(v) + 1) % loop.size())));
//					lowerMesh.add(new Triangle(new Vertex(0f, 0f, 1f), new Vertex(center.x, center.y, zSplit), loop.get((loop.indexOf(v) + 1) % loop.size()), v));
//				}
//			}
			
			// find all loops
			// figure out which loops are outer boundaries and which are holes
			// triangulate the 2D region with holes
			// turn those 2D triangles back into 3D triangles at zSplit
		}
		
		// We have a hashset of segments that form the edge loop of the split.
		
		// 1. start with any segment
		// 2. find next segment that starts with last one
		// 3. repeat until we loop back to the start
		
		
		
		
		
		
		
		
		
//		Vertex center = mesh.center();
		
		// Generate caps for the upper and lower meshes using the edge loop.
//		for (Vertex v : edgeLoop) {
//			upperMesh.add(new Triangle(new Vertex(0f, 0f, -1f), new Vertex(center.x, center.y, zSplit), v, edgeLoop.get((edgeLoop.indexOf(v) + 1) % edgeLoop.size())));
//			lowerMesh.add(new Triangle(new Vertex(0f, 0f, 1f), new Vertex(center.x, center.y, zSplit), edgeLoop.get((edgeLoop.indexOf(v) + 1) % edgeLoop.size()), v));
//		}
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
	
	private Vertex findLoopCenter(List<Vertex> loop) {
	    float xSum = 0f;
	    float ySum = 0f;
	    float zSum = 0f;

	    for (Vertex v : loop) {
	        xSum += v.x;
	        ySum += v.y;
	        zSum += v.z;
	    }

	    int count = loop.size();
	    return new Vertex(xSum / count, ySum / count, zSum / count);
	}
	
	private List<Vertex> buildLoop(List<Segment> unused, float eps) {
	    Segment first = unused.remove(0);

	    List<Vertex> loop = new ArrayList<>();
	    loop.add(first.v1);
	    loop.add(first.v2);

	    while (true) {
	        Vertex end = loop.get(loop.size() - 1);

	        // If we got back to the start, loop is closed.
	        if (loop.size() > 2 && samePoint(end, loop.get(0), eps)) {
	            loop.remove(loop.size() - 1);
	            return loop;
	        }

	        boolean foundNext = false;

	        for (int i = 0; i < unused.size(); i++) {
	            Segment s = unused.get(i);

	            if (samePoint(end, s.v1, eps)) {
	                loop.add(s.v2);
	                unused.remove(i);
	                foundNext = true;
	                break;
	            }

	            if (samePoint(end, s.v2, eps)) {
	                loop.add(s.v1);
	                unused.remove(i);
	                foundNext = true;
	                break;
	            }
	        }

	        if (!foundNext) {
	            // Open loop, something went wrong or mesh is not closed.
	            return loop;
	        }
	    }
	}
	
	private List<List<Vertex>> buildLoops(HashSet<Segment> segments, float eps) {
	    List<Segment> unused = new ArrayList<>(segments);
	    List<List<Vertex>> loops = new ArrayList<>();

	    while (!unused.isEmpty()) {
	        List<Vertex> loop = buildLoop(unused, eps);

	        if (loop.size() >= 3) {
	            loops.add(loop);
	        }
	    }

	    return loops;
	}
	
	private boolean samePoint(Vertex a, Vertex b, float eps) {
	    return Math.abs(a.x - b.x) < eps &&
	           Math.abs(a.y - b.y) < eps &&
	           Math.abs(a.z - b.z) < eps;
	}
}

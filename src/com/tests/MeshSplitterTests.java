package com.tests;

import java.io.File;
import java.io.IOException;

import com.gui.MeshViewer;
import com.mesh_processing.MeshSplitter;
import com.model.Mesh;
import com.model.STLParser;
import com.slicing.STLWriter;

/**
 * Tests for MeshSplitter
 * 
 * @author Zach Brinton
 * @version 4-8-26
 */
public class MeshSplitterTests {

	public static void main(String[] args) throws IOException {
		
		// Get system args.
		File stlFile = new File("src/resources/3DBenchy.stl");
		float zSplit = 10.0f;
		
		// Parse the STL into a mesh.
		Mesh mesh = STLParser.parse(stlFile);
		
		// Split the mesh at zSplit into a lower and upper portion.
		float eps = 0.0001f;
		MeshSplitter splitMesh = new MeshSplitter(mesh, zSplit, eps);
		
		Mesh lowerMesh = splitMesh.getLowerMesh();
		Mesh upperMesh = splitMesh.getUpperMesh();
		
		File lower = new File("src/out/lower_benchy.stl");
		File upper = new File("src/out/upper_benchy.stl");
		
		STLWriter.writeBinary(lower, lowerMesh);
		STLWriter.writeBinary(upper, upperMesh);
		
		MeshViewer.show(lowerMesh, "lower");
		MeshViewer.show(upperMesh, "upper");
	}
}

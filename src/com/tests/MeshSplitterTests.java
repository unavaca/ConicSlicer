package com.tests;

import java.io.File;
import java.io.IOException;

import com.mesh_processing.MeshSplitter;
import com.model.Mesh;
import com.model.STLParser;

/**
 * Tests for MeshSplitter
 * 
 * @author Zach Brinton
 * @version 4-8-26
 */
public class MeshSplitterTests {

	public static void main(String[] args) throws IOException {
		
		// Get system args.
		File stlFile = new File("src/resources/test_cylinder.stl");
		float zSplit = 10.0f;
		
		
		// Parse the STL into a mesh.
		Mesh mesh = STLParser.parse(stlFile);
		
		// Split the mesh at zSplit into a lower and upper portion.
		float eps = 0.0001f;
		MeshSplitter splitMesh = new MeshSplitter(mesh, zSplit, eps);
		System.out.println("lower mesh:");
		System.out.println(splitMesh.getLowerMesh());
		System.out.println("upper mesh:");
		System.out.println(splitMesh.getUpperMesh());
	}

}

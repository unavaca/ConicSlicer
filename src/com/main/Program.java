package com.main;

import java.io.File;

import com.geometry.Bounds;
import com.mapper.ConicMapper;
import com.mapper.ConicMapperUtils;
import com.mesh_processing.MeshSplitter;
import com.model.Mesh;
import com.model.STLParser;
import com.model.Settings;
import com.slicing.GCodeMerger;

/**
 * This program converts an input STL into G-code for our 4-axis printer.
 * 
 * <h2>Axes:</h2>
 * 
 * <ul>
 *   <li><b>X-axis:</b> horizontal, range –162.5 mm to +162.5 mm</li>
 *   <li><b>Z-axis:</b> vertical, range 0 mm to –290 mm</li>
 *   <li><b>B-axis:</b> tilting the nozzle (±150°)</li>
 *   <li><b>C-axis:</b> rotating the build plate (continuous)</li>
 * </ul>
 * 
 * <h2>Args:</h2>
 * 
 * <ol>
 *   <li>STL filepath</li>
 *   <li>Output gcode filepath</li>
 *   <li>Wall perimeters, parsed as int</li>
 *   <li>Fill percentage, parsed as a float from 0.0 to 1.0</li>
 *   <li>zSplit, parsed as a float</li>
 * </ol>
 * 
 * <h2>Pipeline:</h2>
 * 
 * <ol>
 *   <li>Parse the STL into a mesh.</li>
 *   <li>Split the mesh at {@code zSplit} into a lower and upper portion.</li>
 *   <li>Conically slice the lower portion by deforming it into conic space.</li>
 *   <li>Planar slice the deformed lower portion with a normal slicer.</li>
 *   <li>Back-transform the lower portion's G-code into real printer coordinates.</li>
 *   <li>Planar slice the upper portion normally.</li>
 *   <li>Merge the lower conic G-code and upper planar G-code into one final file.</li>
 * </ol>
 * 
 * <p>The goal of this hybrid approach is to use conic slicing where it is most useful,
 * while keeping the rest of the print compatible with ordinary planar slicing.</p>
 * 
 * @author Zach Brinton
 * @version 4-1-26
 */
public final class Program {
	public static void main(String[] args) {
		File stlFile = new File(args[0]);
		File outputFile = new File(args[1]);
		int wallPerimeters = Integer.parseInt(args[2]);
		float fillPercentage = Float.parseFloat(args[3]); // A float value from 0.0 to 1.0.
		float zSplit = Float.parseFloat(args[4]);
		// Build settings used by slicer.
		Settings settings = new Settings(wallPerimeters, fillPercentage);
		
		// Parse the STL into a mesh.
		Mesh mesh = STLParser.parse(stlFile);
		// Calculate bounds of mesh.
		Bounds bounds = new Bounds(mesh);
		
		boolean outsideCone = true; // TODO explain this.
		// TODO explain this
		ConicMapper mapper = new ConicMapper(bounds, outsideCone);
		
		// Split the mesh at zSplit into a lower and upper portion.
		float eps = 0.0001f; // TODO explain this.
		MeshSplitter splitMesh = new MeshSplitter(mesh, zSplit, eps);
		Mesh lower = splitMesh.getLower();
		Mesh upper = splitMesh.getUpper();
		
		// Planarly slice the lower portion.
		File lowerStl = new File("lower.stl");
		File lowerPlanarGcode = new File("lower_planar.gcode");
		
		STLWriter.writeBinary(lowerStl, lower);
		// Add later:
		// PlanarSlicer.sliceToGCode(lowerStl, settings, lowerPlanarGcode);
		// For now:
		ExternalSlicer.sliceToGCode(lowerStl, settings, lowerPlanarGcode);
		
	    // Conically process the upper portion.
	    //Mesh upperDeformed = ConicMapperUtils.inverseMapMesh(upper, mapper);

	    //File upperDeformedStl = new File("upper_deformed.stl");
	    //File upperPlanarGcode = new File("upper_planar.gcode");
	    //File upperConicGcode = new File("upper_conic.gcode");

	    //STLWriter.writeBinary(upperDeformedStl, upperDeformed);
	    // Slice 
	    //ExternalSlicer.sliceToGCode(upperDeformedStl, settings, upperPlanarGcode);
	    // TODO explain this
	    //GCodeBackTransformer.transform(upperPlanarGcode, mapper, settings, upperConicGcode);
		
		// Back-transform the lower portion's G-code into real printer coordinates.
		// Planar slice the upper portion normally.
		// Merge the lower conic G-code and upper planar G-code into one final file.
	    //GCodeMerger.merge(lowerPlanarGcode, upperConicGcode, settings, outputFile);
	}
}

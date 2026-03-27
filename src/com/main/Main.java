package com.main;

import java.io.File;
import java.util.List;

import com.geometry.*;
import com.mapper.*;
import com.mesh_processing.*;
import com.model.*;
import com.output.*;

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
 * @version 3-19-26
 */
public final class Main {
	public static void main(String[] args) throws Exception {
	    Settings settings = loadSettings(args);
	    File inputStl = getInputStl(args);
	    File outputGcode = getOutputGcode(args);

	    // 1) Parse the STL into a mesh
	    Mesh fullMesh = loadMesh(inputStl);

	    // 2) Split the mesh at zSplit into lower and upper portions
	    SplitResult split = splitMesh(fullMesh, settings);
	    Mesh lowerMesh = split.lower;
	    Mesh upperMesh = split.upper;

	    // 3) Conically process the lower portion
	    File lowerConicGcode = buildLowerConicGcode(lowerMesh, outputGcode, settings);

	    // 4) Planarly process the upper portion
	    File upperPlanarGcode = buildUpperPlanarGcode(upperMesh, outputGcode, settings);

	    // 5) Merge both G-code files into the final output
	    mergeFinalGcode(lowerConicGcode, upperPlanarGcode, outputGcode, settings);
	}
    
	private static Settings loadSettings(String[] args) {
	    Settings settings = Settings.fromArgs(args);
	    settings.validate();
	    return settings;
	}

	private static File getInputStl(String[] args) {
	    return new File(args[0]);
	}

	private static File getOutputGcode(String[] args) {
	    return new File(args[1]);
	}

	private static Mesh loadMesh(File stlFile) throws Exception {
	    List<Triangle> triangles = STLParser.parse(stlFile);
	    return new Mesh(triangles);
	}

	private static SplitResult splitMesh(Mesh fullMesh, Settings settings) {
	    return MeshSplitter.splitAtZ(fullMesh, settings.zSplit(), settings.eps());
	}

	private static File buildLowerConicGcode(Mesh lowerMesh, File outputGcode, Settings settings) throws Exception {
	    ConicMapper mapper = createConicMapper(lowerMesh);

	    Mesh lowerDeformed = ConicMapperUtils.inverseMapMesh(lowerMesh, mapper);

	    File deformedStl = new File(outputGcode.getParentFile(), "lower_deformed.stl");
	    STLWriter.writeBinary(deformedStl, lowerDeformed);

	    ExternalSlicer slicer = new ExternalSlicer(settings.slicerCommand());
	    File lowerPlanarGcode = new File(outputGcode.getParentFile(), "lower_planar.gcode");
	    slicer.sliceToGCode(deformedStl, settings, lowerPlanarGcode);

	    File lowerConicGcode = new File(outputGcode.getParentFile(), "lower_conic.gcode");
	    GCodeBackTransformer.transform(lowerPlanarGcode, mapper, settings, lowerConicGcode);

	    return lowerConicGcode;
	}

	private static File buildUpperPlanarGcode(Mesh upperMesh, File outputGcode, Settings settings) throws Exception {
	    File upperStl = new File(outputGcode.getParentFile(), "upper.stl");
	    STLWriter.writeBinary(upperStl, upperMesh);

	    ExternalSlicer slicer = new ExternalSlicer(settings.slicerCommand());
	    File upperPlanarGcode = new File(outputGcode.getParentFile(), "upper_planar.gcode");
	    slicer.sliceToGCode(upperStl, settings, upperPlanarGcode);

	    return upperPlanarGcode;
	}

	private static void mergeFinalGcode(File lowerConicGcode, File upperPlanarGcode,
	                                    File outputGcode, Settings settings) throws Exception {
	    GCodeMerger.merge(lowerConicGcode, upperPlanarGcode, settings, outputGcode);
	}

	private static ConicMapper createConicMapper(Mesh mesh) {
	    Bounds bounds = mesh.bounds();
	    float cx = (bounds.minX + bounds.maxX) / 2f;
	    float cy = (bounds.minY + bounds.maxY) / 2f;
	    return new ConicMapper(cx, cy, true);
	}
}
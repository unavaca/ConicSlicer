package com.conicslicer;

import java.io.File;
import java.util.List;

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
    	
    }
}
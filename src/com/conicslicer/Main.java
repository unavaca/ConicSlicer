package com.conicslicer;

import java.io.File;
import java.util.List;

/**
 * Completes the process of generating conic G-code from an STL file.
 * 
 * 
 * <p>Ignore this:</p>@deprecated
 * <ol>
 * <li>Parse the STL: {@link STLParser}</li>
 * <li>Refine the mesh: {@link MeshRefiner}</li>
 * <li>Transform to conic space: {@link ConicMapper}</li>
 * <li>Slice: {@link Slicer}</li>
 * <li>Generate planar G-code: {@link GCodeGenerator}</li>
 * <li>Back-transform to conical G-code: {@link GCodeBackTransformer}</li>
 * <li>Save the result</li>
 * </ol>
 * 
 * <p>New pipeline</p>
 * <ol>
 * <li>Load triangles: {@link STLParser}</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ol>
 * 
 * 
 * <p>Accepts command-line arguments for the STL path, cone center and direction, layer height,
 * extrusion width, and output file names.</p>
 * 
 * @author Zach Brinton
 * @version 3-6-26
 */
public final class Main {
    public static void main(String[] args) throws Exception {
        // args:
        // 0: stl file
        // 1: out gcode file
        // 2: wall perimeters (int)
        // 3: fill percentage (0..1)
        // 4: zSplit (float)

        Settings s = Settings.fromArgs(args);
        s.validate();

        File inStl = new File(args[0]);
        File outGcode = new File(args[1]);

        // ---- 1) Load STL -> Mesh ----
        List<Triangle> tris = STLParser.parse(inStl);
        Mesh full = new Mesh(tris);

        // ---- 2) Choose conic axis center (cx, cy) ----
        // For now: use mesh XY center. Later you can add optional args.
        Bounds b = full.bounds();
        float cx = (b.minX + b.maxX) * 0.5f;
        float cy = (b.minY + b.maxY) * 0.5f;

        // Outside-cone is the usual default for outward overhangs.
        ConicMapper mapper = new ConicMapper(cx, cy, /*outsideCone=*/true);

        // ---- 3) Split mesh at zSplit ----
        // eps is for numeric tolerance during triangle cutting.
        float eps = s.eps;
        SplitResult split = MeshSplitter.splitAtZ(full, s.zSplit, eps);
        Mesh lower = split.lower;
        Mesh upper = split.upper;

        // (Optional but recommended) refine the LOWER mesh before conic mapping
        // lower = MeshRefiner.refine(lower, s.refineLevels);

        // ---- 4) Conic pipeline for LOWER half ----
        // 4a) Deform lower mesh in-place into conic space (inverse map)
        Mesh lowerDeformed = ConicMapperUtils.inverseMapMesh(lower, mapper);

        // 4b) Planar slice the deformed lower mesh -> planar gcode
        // Use an external slicer here (Cura, Slic3r, etc.)
        ExternalSlicer slicer = new ExternalSlicer(s.slicerCommand);

        File gLowerPlanar = new File(outGcode.getParentFile(), "lower_deformed_planar.gcode");
        File stlLowerDeformed = new File(outGcode.getParentFile(), "lower_deformed.stl");
        STLWriter.writeBinary(stlLowerDeformed, lowerDeformed); // optional helper (or skip if slicer can ingest mesh directly)

        slicer.sliceToGCode(stlLowerDeformed, s, gLowerPlanar);

        // 4c) Back-transform the planar gcode into conic gcode for the 4-axis printer
        File gLowerConic = new File(outGcode.getParentFile(), "lower_conic.gcode");
        GCodeBackTransformer.transform(gLowerPlanar, mapper, s, gLowerConic);

        // ---- 5) Normal planar slicing for UPPER half ----
        File gUpperPlanar = new File(outGcode.getParentFile(), "upper_planar.gcode");
        File stlUpper = new File(outGcode.getParentFile(), "upper.stl");
        STLWriter.writeBinary(stlUpper, upper); // optional helper

        slicer.sliceToGCode(stlUpper, s, gUpperPlanar);

        // ---- 6) Merge lower + upper into final ----
        // Merge policy:
        // - keep ONE header (from lower)
        // - keep ONE footer (from upper)
        // - ensure extrusion mode consistency (recommended: normalize both beforehand)
        GCodeMerger.merge(gLowerConic, gUpperPlanar, s, outGcode);
    }
}
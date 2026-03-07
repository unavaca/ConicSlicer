package com.conicslicer;

/**
 * @author Zach Brinton
 * @version 3-6-26
 */
public final class Settings {
    public final int walls;
    public final float infill01;
    public final float zSplit;

    // tolerances + defaults
    public final float eps = 1e-5f;

    // external slicer command (set this to your slicer CLI string)
    public final String slicerCommand = "cura"; // placeholder

    // which gcode letter for rotation axis on your printer (U or A)
    public final char rotAxisLetter = 'U';

    // (optional) conic settings
    public final boolean outsideCone = true;

    private Settings(int walls, float infill01, float zSplit) {
        this.walls = walls;
        this.infill01 = infill01;
        this.zSplit = zSplit;
    }

    public static Settings fromArgs(String[] args) {
        if (args.length < 5) {
            throw new IllegalArgumentException(
                "Usage: <in.stl> <out.gcode> <walls:int> <infill:0..1> <zSplit:float>"
            );
        }
        int walls = Integer.parseInt(args[2]);
        float infill = Float.parseFloat(args[3]);
        float zSplit = Float.parseFloat(args[4]);
        return new Settings(walls, infill, zSplit);
    }

    public void validate() {
        if (walls < 0) throw new IllegalArgumentException("walls must be >= 0");
        if (infill01 < 0f || infill01 > 1f) throw new IllegalArgumentException("infill must be in [0,1]");
        // zSplit can be any float, but you might want it >= 0 depending on your coordinate conventions
    }
}

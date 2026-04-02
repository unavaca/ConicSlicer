package com.model;

/**
 * Contains the settings passed in by the user on run. Used for slicing.
 * 
 * @author Zach Brinton
 * @version 4-1-26
 */
public final class Settings {
    public final int wallPerimeters;
    public final float fillPercentage;

    /**
     * Constructs a settings object for slicing.
     * 
     * @param wallPerimeters number of wall perimeters to print
     * @param fillPercentage infill percentage from 0.0 to 1.0
     * @throws IllegalArgumentException if values are invalid
     */
    public Settings(int wallPerimeters, float fillPercentage) {
        if (wallPerimeters < 0)
            throw new IllegalArgumentException("wallPerimeters must be at least 0.");

        if (fillPercentage < 0.0f || fillPercentage > 1.0f)
            throw new IllegalArgumentException("fillPercentage must be between 0.0 and 1.0.");

        this.wallPerimeters = wallPerimeters;
        this.fillPercentage = fillPercentage;
    }
    
    public int getWallPerimeters() {
    	return wallPerimeters;
    }
    
    public float getFillPercentage() {
    	return fillPercentage;
    }
}

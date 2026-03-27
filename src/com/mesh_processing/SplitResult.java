package com.mesh_processing;

import com.model.Mesh;

public class SplitResult {
	public final Mesh upper;
	public final Mesh lower;
	
	public SplitResult(Mesh upper, Mesh lower) {
		this.upper = upper;
		this.lower = lower;
	}
}

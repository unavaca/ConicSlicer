package com.conicslicer;

public class SplitResult {
	public final Mesh upper;
	public final Mesh lower;
	
	public SplitResult(Mesh upper, Mesh lower) {
		this.upper = upper;
		this.lower = lower;
	}
}

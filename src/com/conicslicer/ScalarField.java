package com.conicslicer;

/**
 * @author GPT 5.2
 * @version 3-5-26
 */
@FunctionalInterface
public interface ScalarField {
    /**
     * Evaluates a scalar function at point p.
     * The surface is defined by f(p) = 0.
     *
     * @param p input point
     * @return scalar value at p
     */
    float eval(Vertex p);
}

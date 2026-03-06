package com.conicslicer;

/**
 * Conic slicing layer as an implicit surface f(p) = 0.
 *
 * Outside-cone: z - r = s   => f = z - r - s
 * Inside-cone:  z + r = s   => f = z + r - s
 *
 * where r = sqrt((x-cx)^2 + (y-cy)^2)
 * 
 * @author Zach Brinton and GPT 5.2
 * @version 3-6-26
 */
public final class ConicField implements ScalarField {
    public final float cx, cy;
    public final float sign; // +1 outside, -1 inside
    public final float s;    // layer value

    public ConicField(float cx, float cy, float sign, float s) {
        if (sign != 1f && sign != -1f) throw new IllegalArgumentException("sign must be +1 or -1");
        this.cx = cx;
        this.cy = cy;
        this.sign = sign;
        this.s = s;
    }

    @Override
    public float eval(Vertex p) {
        float dx = p.x - cx;
        float dy = p.y - cy;
        float r = (float) Math.sqrt(dx * dx + dy * dy);
        return p.z - sign * r - s;
    }
}
package com.mapper;

import java.util.ArrayList;
import java.util.List;

import com.geometry.Triangle;
import com.geometry.Vertex;
import com.model.Mesh;

/**
 * @version 3-6-26
 * @author Zach Brinton and GPT 5.2
 */
public final class ConicMapperUtils {
    private ConicMapperUtils() {}

    public static Mesh inverseMapMesh(Mesh mesh, ConicMapper mapper) {
        List<Triangle> out = new ArrayList<>(mesh.triangles().size());
        for (Triangle t : mesh.triangles()) {
            Vertex n = mapper.inverseMap(t.normal);
            Vertex a = mapper.inverseMap(t.v1);
            Vertex b = mapper.inverseMap(t.v2);
            Vertex c = mapper.inverseMap(t.v3);
            out.add(new Triangle(n, a, b, c));
        }
        return new Mesh(out);
    }
}

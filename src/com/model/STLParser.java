package com.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.geometry.Triangle;
import com.geometry.Vertex;

/**
 * Reads a binary or ASCII STL file and constructs a list of {@link Triangle} objects.
 * 
 * <p>Each {@link Triangle} stores its three {@link Vertex} points (with x, y, z) and the normal.</p>
 * <p>When parsing an ASCII STL, recognise the facet normal/vertex structure.</p>
 * <p>When parsing a binary STL, read the 80-byte header and 50-byte records per triangle.</p>
 * <p>An example of an ASCII STL and a binary STL can be found in the resources folder.</p>
 * 
 * @author Zach Brinton and GPT 5.2
 * @version 3-6-26
 */
public final class STLParser {

    private STLParser() {
        // utility class
    }

    /**
     * Parses an STL file and returns the triangles.
     *
     * @param file the STL file
     * @return list of triangles (possibly empty)
     * @throws IOException if the file can't be read or is malformed
     */
    public static List<Triangle> parse(File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("file must be non-null");
        if (!file.exists()) throw new IOException("File does not exist: " + file);
        if (!file.isFile()) throw new IOException("Not a file: " + file);

        if (isAscii(file)) {
            return parseAscii(file);
        } else {
            return parseBinary(file);
        }
    }

    /**
     * Heuristically detects whether the STL is ASCII.
     *
     * <p>Approach:</p>
     * <ol>
     *   <li>If file size matches binary layout (84 + 50*n), assume binary.</li>
     *   <li>Otherwise, read first 80 bytes. If it doesn't start with "solid", assume binary.</li>
     *   <li>Otherwise, scan a small chunk for non-printable bytes; if found, assume binary.</li>
     * </ol>
     *
     * @param file the STL file
     * @return true if likely ASCII, false if likely binary
     * @throws IOException if reading fails
     */
    private static boolean isAscii(File file) throws IOException {
        long size = file.length();

        // Binary STLs are: 80-byte header + 4-byte uint triangle count + 50 bytes/triangle
        if (size >= 84 && (size - 84) % 50 == 0) {
            return false;
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] header = new byte[80];
            int read = in.read(header);
            if (read < 80) {
                return false;
            }

            String headerStr = new String(header, StandardCharsets.US_ASCII).trim();
            if (!headerStr.startsWith("solid")) {
                return false;
            }

            // Scan the next chunk for non-printable control characters.
            byte[] chunk = new byte[256];
            int len = in.read(chunk);
            for (int i = 0; i < len; i++) {
                int b = chunk[i] & 0xFF;
                if (b < 0x20 && b != '\n' && b != '\r' && b != '\t') {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Parses an ASCII STL file.
     *
     * <p>Expected structure (whitespace flexible):</p>
     * <pre>
     * solid name
     *   facet normal nx ny nz
     *     outer loop
     *       vertex x y z
     *       vertex x y z
     *       vertex x y z
     *     endloop
     *   endfacet
     * endsolid name
     * </pre>
     *
     * @param file ASCII STL file
     * @return list of triangles
     * @throws IOException if reading fails or file is malformed
     */
    private static List<Triangle> parseAscii(File file) throws IOException {
        List<Triangle> tris = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.US_ASCII))) {
            String line;

            Vertex normal = null;
            Vertex[] verts = new Vertex[3];
            int vCount = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // facet normal nx ny nz
                if (line.startsWith("facet normal")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length < 5) {
                        throw new IOException("Malformed facet normal line: " + line);
                    }
                    float nx = Float.parseFloat(parts[2]);
                    float ny = Float.parseFloat(parts[3]);
                    float nz = Float.parseFloat(parts[4]);
                    normal = new Vertex(nx, ny, nz);

                    vCount = 0; // reset for this facet
                    continue;
                }

                // vertex x y z
                if (line.startsWith("vertex")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length < 4) {
                        throw new IOException("Malformed vertex line: " + line);
                    }
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);

                    if (vCount < 3) {
                        verts[vCount] = new Vertex(x, y, z);
                        vCount++;
                    } else {
                        // Some broken files might have extra vertices
                        throw new IOException("Too many vertices for facet near line: " + line);
                    }
                    continue;
                }

                // endfacet => build triangle if we have everything
                if (line.startsWith("endfacet")) {
                    if (normal == null) {
                        throw new IOException("endfacet before facet normal");
                    }
                    if (vCount != 3) {
                        throw new IOException("Facet did not have 3 vertices (had " + vCount + ")");
                    }

                    tris.add(new Triangle(normal, verts[0], verts[1], verts[2]));
                    normal = null;
                    vCount = 0;
                }
            }
        }

        return tris;
    }

    /**
     * Parses a binary STL file.
     *
     * <p>Binary layout:</p>
     * <ul>
     *   <li>80 bytes header (ignored)</li>
     *   <li>4 bytes little-endian unsigned int: triangle count</li>
     *   <li>Per triangle (50 bytes):</li>
     *   <ul>
     *     <li>12 floats little-endian: normal (3), v1 (3), v2 (3), v3 (3)</li>
     *     <li>2 bytes: attribute byte count (ignored)</li>
     *   </ul>
     * </ul>
     *
     * @param file binary STL file
     * @return list of triangles
     * @throws IOException if reading fails or file is malformed
     */
    private static List<Triangle> parseBinary(File file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            // 80-byte header
            byte[] header = new byte[80];
            in.readFully(header);

            long triCountUnsigned = readUInt32LE(in);
            if (triCountUnsigned > Integer.MAX_VALUE) {
                throw new IOException("Triangle count too large: " + triCountUnsigned);
            }
            int triCount = (int) triCountUnsigned;

            List<Triangle> tris = new ArrayList<>(triCount);

            // Each triangle record is 50 bytes
            byte[] record = new byte[50];
            ByteBuffer bb = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < triCount; i++) {
                try {
                    in.readFully(record);
                } catch (EOFException eof) {
                    throw new IOException("Unexpected EOF reading triangle " + i + " of " + triCount, eof);
                }

                bb.rewind();

                float nx = bb.getFloat();
                float ny = bb.getFloat();
                float nz = bb.getFloat();

                float x1 = bb.getFloat();
                float y1 = bb.getFloat();
                float z1 = bb.getFloat();

                float x2 = bb.getFloat();
                float y2 = bb.getFloat();
                float z2 = bb.getFloat();

                float x3 = bb.getFloat();
                float y3 = bb.getFloat();
                float z3 = bb.getFloat();

                // attribute byte count (2 bytes) - ignored
                bb.getShort();

                Vertex normal = new Vertex(nx, ny, nz);
                Vertex v1 = new Vertex(x1, y1, z1);
                Vertex v2 = new Vertex(x2, y2, z2);
                Vertex v3 = new Vertex(x3, y3, z3);

                tris.add(new Triangle(normal, v1, v2, v3));
            }

            return tris;
        }
    }

    /**
     * Reads a little-endian unsigned 32-bit integer from the stream.
     *
     * @param in input stream
     * @return value in range [0, 2^32-1] stored in a long
     * @throws IOException if reading fails
     */
    private static long readUInt32LE(DataInputStream in) throws IOException {
        int b0 = in.readUnsignedByte();
        int b1 = in.readUnsignedByte();
        int b2 = in.readUnsignedByte();
        int b3 = in.readUnsignedByte();
        return ((long) b0)
            | ((long) b1 << 8)
            | ((long) b2 << 16)
            | ((long) b3 << 24);
    }
}

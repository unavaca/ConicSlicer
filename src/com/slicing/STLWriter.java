package com.slicing;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.geometry.Vertex;
import com.geometry.Triangle;
import com.model.Mesh;

/**
 * 
 * @author Zach Brinton and GPT 5.4
 * @version 4-9-26
 */
public class STLWriter {

	/**
	 * Writes the given mesh to a binary STL file.
	 *
	 * Binary STL format:
	 * - 80 byte header
	 * - 4 byte triangle count (little endian)
	 * - for each triangle:
	 *   - 12 bytes normal (3 floats)
	 *   - 12 bytes vertex 1
	 *   - 12 bytes vertex 2
	 *   - 12 bytes vertex 3
	 *   - 2 bytes attribute byte count (usually 0)
	 *
	 * @param outFile the file to write to
	 * @param mesh the mesh to write
	 */
	public static void writeBinary(File outFile, Mesh mesh) {
		try (DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outFile)))) {

			// 80-byte header
			byte[] header = new byte[80];
			byte[] title = "Binary STL written by custom slicer".getBytes(StandardCharsets.US_ASCII);
			System.arraycopy(title, 0, header, 0, Math.min(title.length, 80));
			out.write(header);

			// Triangle count
			int triangleCount = mesh.size();
			writeIntLE(out, triangleCount);

			// Triangle records
			for (Triangle tri : mesh) {
				Vertex normal = tri.normal;
				Vertex v1 = tri.v1;
				Vertex v2 = tri.v2;
				Vertex v3 = tri.v3;

				writePointLE(out, normal);
				writePointLE(out, v1);
				writePointLE(out, v2);
				writePointLE(out, v3);

				// Attribute byte count
				writeShortLE(out, 0);
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to write binary STL to: " + outFile, e);
		}
	}

	private static void writePointLE(DataOutputStream out, Vertex v) throws IOException {
		writeFloatLE(out, v.x);
		writeFloatLE(out, v.y);
		writeFloatLE(out, v.z);
	}

	private static void writeFloatLE(DataOutputStream out, float value) throws IOException {
		writeIntLE(out, Float.floatToIntBits(value));
	}

	private static void writeIntLE(DataOutputStream out, int value) throws IOException {
		out.writeByte(value & 0xFF);
		out.writeByte((value >>> 8) & 0xFF);
		out.writeByte((value >>> 16) & 0xFF);
		out.writeByte((value >>> 24) & 0xFF);
	}

	private static void writeShortLE(DataOutputStream out, int value) throws IOException {
		out.writeByte(value & 0xFF);
		out.writeByte((value >>> 8) & 0xFF);
	}
}
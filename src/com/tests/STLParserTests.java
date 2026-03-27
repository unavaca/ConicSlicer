package com.tests;

import java.io.File;
import java.io.IOException;

import com.geometry.Triangle;
import com.main.*;
import com.model.STLParser;

public class STLParserTests {

	public static void main(String[] args) throws IOException {
		testAscii();
	}
	
	private static void testAscii() throws IOException {
		int i = 0;
		for (Triangle t : STLParser.parse(new File("src/resources/test_cylinder.stl"))) {
			System.out.print(i++ + ": ");
			System.out.println(t);
		}
	}
	
	private static void testBinary() throws IOException {
		int i = 0;
		for (Triangle t : STLParser.parse(new File("src/resources/support_cube_BinaryExample.STL"))) {
			System.out.println(i++ + ": ");
			System.out.println(t);
		}
	}

}

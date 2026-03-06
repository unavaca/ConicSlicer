package com.tests;

import java.io.File;
import java.io.IOException;

import com.conicslicer.*;

public class STLParserTests {

	public static void main(String[] args) throws IOException {
		for (Triangle t : STLParser.parse(new File("src/resources/test_cylinder.stl"))) {
			System.out.println(t);
		}
	}

}

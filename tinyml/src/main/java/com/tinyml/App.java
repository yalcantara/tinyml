package com.tinyml;

import com.tinyml.structs.Grid;
import com.tinyml.structs.Loader;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		Grid g = Loader.csv("files/test.data");
		g.print();
	}
}

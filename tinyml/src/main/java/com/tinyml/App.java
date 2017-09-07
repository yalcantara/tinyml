package com.tinyml;

import com.tinyml.structs.Mat;
import com.tinyml.structs.Vec;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Mat a = new Mat(2, 2, new float[] { 1, 2, 3, 4 });

		Vec b = new Vec(new float[] { 1, 2 });

		Vec c = a.dot(b);

		c.print();
	}
}

package com.tinyml.structs;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.distribution.impl.NormalDistribution;
import org.nd4j.linalg.factory.Nd4j;

public class Mat {

	private static final int MAX_PRINT_ROWS = 500;
	private static final int MAX_PRINT_COLS = 500;

	private final INDArray arr;

	private final int m;
	private final int n;

	public Mat(int m, int n) {

		arr = Nd4j.create(new int[] { m, n }, 'c');
		this.m = m;
		this.n = n;
	}

	private Mat(INDArray arr) {

		int[] shape = arr.shape();
		if (shape.length != 2) {
			throw new IllegalArgumentException("The shape of the arr must be 2. Got: " + shape.length + ".");
		}
		this.arr = arr;
		this.m = shape[0];
		this.n = shape[1];
	}

	public Mat(float[] arr, int m, int n) {
		this.m = m;
		this.n = n;
		this.arr = Nd4j.create(new int[] { m, n }, 'c');

		int l = m * n;
		for (int i = 0; i < l; i++) {
			this.arr.putScalar(i, arr[i]);
		}
	}

	public Mat(double[] arr, int m, int n) {
		this.m = m;
		this.n = n;
		this.arr = Nd4j.create(new int[] { m, n }, 'c');

		int l = m * n;
		for (int i = 0; i < l; i++) {
			this.arr.putScalar(i, arr[i]);
		}
	}

	public static Mat rand(int d) {
		return rand(d, d);
	}

	public static Mat rand(int m, int n) {
		NormalDistribution nd = new NormalDistribution(0.0, 1.0);
		return new Mat(Nd4j.rand(new int[] { m, n }, nd));
	}

	private void check(int i, int j) {
		if (i >= m || i < 1) {
			throw new IndexOutOfBoundsException(
					"The row parameter is " + "out of bounds. Rows " + m + ", row parameter: " + i + ".");
		}

		if (j >= n || j < 1) {
			throw new IndexOutOfBoundsException(
					"The col parameter is " + "out of bounds. Columns " + n + ", col parameter: " + j + ".");
		}
	}

	public void set(int i, int j, double val) {
		set(i, j, (float) val);
	}

	public void set(int i, int j, float val) {
		check(i, j);
		int idx = i * n + j;
		arr.putScalar(idx, val);
	}

	public void inc(int i, int j, double val) {
		inc(i, j, (int) val);
	}

	public void inc(int i, int j, float val) {
		check(i, j);
		int idx = i * n + j;
		arr.putScalar(idx, arr.getFloat(idx) + val);
	}

	public int cols() {
		return n;
	}

	public int rows() {
		return m;
	}

	public float get(int i, int j) {
		check(i, j);
		int idx = i * n + j;
		return arr.getFloat(idx);
	}

	public int length() {
		return arr.length();
	}
}

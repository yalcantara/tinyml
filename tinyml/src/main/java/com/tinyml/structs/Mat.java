package com.tinyml.structs;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.distribution.impl.NormalDistribution;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Mat {

	private static final int MAX_PRINT_ROWS = 300;
	private static final int MAX_PRINT_COLS = 300;

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

	public Mat(int m, int n, float[] arr) {
		this.m = m;
		this.n = n;
		this.arr = Nd4j.create(new int[] { m, n }, 'c');

		int l = m * n;
		for (int i = 0; i < l; i++) {
			this.arr.putScalar(i, arr[i]);
		}
	}

	public Mat(int m, int n, double[] arr) {
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
		if (i >= m || i < 0) {
			throw new IndexOutOfBoundsException(
					"The row parameter is " + "out of bounds. Rows " + m + ", row parameter: " + i + ".");
		}

		if (j >= n || j < 0) {
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

	public Vec col(int idx) {
		return new Vec(arr.getColumn(idx));
	}

	public Vec row(int idx) {
		return new Vec(arr.getRow(idx));
	}

	public Mat selectRows(int start, int end) {

		int l = end - start;

		if (end <= start) {
			throw new IllegalArgumentException(
					"The 'end' parameter must be higher than the 'start' parameter. Expected >  " + start + ", but got "
							+ end + " instead.");
		}

		if (l > m) {
			throw new IllegalArgumentException(
					"For roll=false the length of the selection can not be higher than this matrix number of rows. Number of rows: "
							+ m + ", selection length: " + l + ".");
		}

		if (end > m) {
			throw new IllegalArgumentException(
					"For roll=false the end parameter must be less or equals than the number of rows. Number of rows: "
							+ m + ", end parameter: " + m + ".");
		}

		int[] idx = new int[l];

		for (int i = 0; i < l; i++) {
			idx[i] = start + i;
		}

		return new Mat(arr.getRows(idx));
	}

	public Mat stdScale() {
		Vec mean = colMean();
		Vec std = colStdev();

		Mat ans = clone();
		ans.arr.subiRowVector(mean.arr);
		ans.arr.diviRowVector(std.arr);

		return ans;
	}

	public float sum() {
		return arr.sumNumber().floatValue();
	}

	public Vec colMin() {
		INDArray newarr = arr.min(new int[] { 0 });

		return new Vec(newarr);
	}

	public Vec colMax() {
		INDArray newarr = arr.max(new int[] { 0 });

		return new Vec(newarr);
	}

	public Vec colSum() {
		return new Vec(arr.sum(new int[] { 0 }));
	}

	public Vec colVar() {
		return new Vec(arr.var(new int[] { 0 }));
	}

	public Vec colStdev() {
		return new Vec(arr.std(new int[] { 0 }));
	}

	public Vec colMean() {
		return new Vec(arr.mean(new int[] { 0 }));
	}

	public Vec rowMax() {
		return new Vec(arr.max(new int[] { 1 }));
	}

	public Vec rowMin() {
		return new Vec(arr.min(new int[] { 1 }));
	}

	public Vec rowSum() {
		return new Vec(arr.sum(new int[] { 1 }));
	}

	public Vec rowVar() {
		return new Vec(arr.var(new int[] { 1 }));
	}

	public Vec rowStdev() {
		return new Vec(arr.std(new int[] { 1 }));
	}

	public Vec rowMean() {
		return new Vec(arr.mean(new int[] { 1 }));
	}

	public Mat add(Mat b) {

		int bm = b.m;
		int bn = b.n;
		if (m != bm || n != bn) {
			throw new IllegalArgumentException(
					"Matrix dimensions must be the same. This " + m + "x" + n + ", other " + bm + "x" + bn);
		}

		INDArray newarr = arr.add(b.arr);

		return new Mat(newarr);
	}

	public Mat add(double val) {
		return add((float) val);
	}

	public Mat add(float val) {

		INDArray newarr = arr.add(val);

		return new Mat(newarr);
	}

	public Mat sub(float val) {

		INDArray newarr = arr.sub(val);

		return new Mat(newarr);
	}

	public Mat sub(Mat b) {

		int bm = b.m;
		int bn = b.n;
		if (m != bm || n != bn) {
			throw new IllegalArgumentException(
					"Matrix dimensions must be the same. This " + m + "x" + n + ", other " + bm + "x" + bn);
		}

		INDArray newarr = arr.add(b.arr);

		return new Mat(newarr);
	}

	public void assign(float val) {
		arr.assign(val);
	}

	public Mat pow(float exp) {

		INDArray newarr = Transforms.pow(arr, exp);
		return new Mat(newarr);
	}

	public Mat dot(Mat b) {
		int bm = b.m;
		int bn = b.n;

		if (n != bm) {
			throw new IllegalArgumentException("Invalid matrix dimension for multiplication. This " + m + "x" + n
					+ ", other " + bm + "x" + bn + ".");
		}

		INDArray newarr = Nd4j.createUninitialized(new int[] { m, bn }, 'c');
		arr.mmul(b.arr, newarr);

		return new Mat(newarr);
	}

	public Mat affine(Mat w, Vec b) {

		int wm = w.m;
		int wn = w.n;
		int bl = b.length();

		if (n != wm) {
			throw new IllegalArgumentException("Invalid matrix dimension for multiplication. This " + m + "x" + n
					+ ", other " + wm + "x" + wn + ".");
		}

		if (wn != bl) {
			throw new IllegalArgumentException(
					"Invalid vector dimension for addition broadcast. Expected " + n + ", but got " + bl + " instead.");
		}

		Mat c = dot(w);
		c.arr.addiRowVector(b.arr);

		return c;
	}

	public Mat mult(Mat b) {
		int om = b.m;
		int on = b.n;

		if (n != on || m != om) {
			throw new IllegalArgumentException("Invalid matrix dimension for element-wise multiplication. Expected "
					+ "the same dimension for both matrices, but instead go: this " + m + "x" + n + ", other " + om
					+ "x" + on + ".");
		}

		INDArray newarr = arr.mul(b.arr);

		return new Mat(newarr);
	}

	public Mat mult(double scalar) {
		return mult((float) scalar);
	}

	public Mat mult(float scalar) {

		INDArray newarr = arr.mul(scalar);

		return new Mat(newarr);
	}

	public Mat div(double scalar) {
		return div((float) scalar);
	}

	public Mat div(float scalar) {
		return mult(1.0f / scalar);
	}

	public Vec dot(Vec x) {
		int l = x.length();

		if (n != l) {
			throw new IllegalArgumentException(
					"Invalid matrix dimension for multiplication. This " + m + "x" + n + ", vector length " + l + ".");
		}

		INDArray newarr = Nd4j.createUninitialized(l);
		Nd4j.getBlasWrapper().level2().gemv('c', 'n', 1, arr, x.arr, 0, newarr);

		return new Vec(newarr);
	}

	public Mat transp() {
		return new Mat(arr.transpose());
	}

	@Override
	public Mat clone() {
		return new Mat(arr.dup());
	}

	public float[][] toArray() {
		float[][] arr = new float[m][n];

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				arr[i][j] = get(i, j);
			}
		}

		return arr;
	}

	public void print() {
		print(System.out, MAX_PRINT_ROWS, MAX_PRINT_COLS);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		print(sb, MAX_PRINT_ROWS, MAX_PRINT_COLS);
		return sb.toString();
	}

	public void print(Appendable out, int maxRows, int maxCols) {

		maxRows = (maxRows < m) ? maxRows : m;
		maxCols = (maxCols < n) ? maxCols : n;

		try {
			if (maxRows < m || maxCols < n) {
				out.append("Mat " + m + "x" + n + "  (truncated)\n");
			} else {
				out.append("Mat " + m + "x" + n + "\n");
			}

			NumberFormat f = DecimalFormat.getNumberInstance();
			f.setMaximumFractionDigits(4);
			f.setMinimumFractionDigits(4);
			f.setGroupingUsed(true);

			int[] maxLength = new int[maxCols];

			for (int j = 0; j < maxCols; j++) {
				for (int i = 0; i < maxRows; i++) {

					String str = f.format(get(i, j));

					maxLength[j] = Math.max(maxLength[j], str.length());
				}
			}

			for (int i = 0; i < maxRows; i++) {
				for (int j = 0; j < maxCols; j++) {
					if (j > 0) {
						out.append("  ");
					}
					String str = f.format(get(i, j));
					int leading = maxLength[j] - str.length();
					for (int s = 0; s < leading; s++) {
						out.append(" ");
					}

					out.append(str);
				}
				out.append("\n");
			}

			out.append("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

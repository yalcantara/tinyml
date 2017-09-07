package com.tinyml.structs;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.distribution.impl.NormalDistribution;
import org.nd4j.linalg.factory.Nd4j;

public class Vec {

	public static final int MAX_PRINT = 300;

	final INDArray arr;

	private final int length;

	Vec(INDArray arr) {
		this.arr = arr;
		this.length = arr.length();
	}

	public Vec(float[] arr) {
		this.length = arr.length;

		this.arr = Nd4j.createUninitialized(length);
		for (int i = 0; i < length; i++) {
			this.arr.putScalar(i, arr[i]);
		}
	}

	public Vec(double[] arr) {
		this.length = arr.length;

		this.arr = Nd4j.createUninitialized(length);
		for (int i = 0; i < length; i++) {
			this.arr.putScalar(i, (float) arr[i]);
		}
	}

	public static Vec rand(int d) {
		NormalDistribution nd = new NormalDistribution(0, 1);

		return new Vec(Nd4j.rand(new int[] { d }, nd));
	}

	public Vec(int d) {
		this.arr = Nd4j.create(d);
		this.length = d;
	}

	private void check(int idx) {
		if (idx >= length || idx < 1) {
			throw new IndexOutOfBoundsException(
					"The idx parameter is out of bounds. Got " + idx + ", length: " + length + ".");
		}
	}

	public Vec add(Vec b) {

		int d = length();

		int bd = b.length();
		if (d != bd) {
			throw new IllegalArgumentException("Vector dimensions must be the same. This " + b + ", other " + bd + ".");
		}

		INDArray newarr = arr.add(b.arr);

		return new Vec(newarr);
	}

	public float dot(Vec b) {

		int bd = b.length();
		if (length != bd) {
			throw new IllegalArgumentException("Vector dimensions must be the same. This " + b + ", other " + bd + ".");
		}

		return arr.mul(b.arr).sumNumber().floatValue();
	}

	public float get(int idx) {
		check(idx);
		return arr.getFloat(idx);
	}

	public void set(int idx, double val) {
		set(idx, val);
	}

	public void set(int idx, float val) {
		check(idx);
		arr.putScalar(idx, val);
	}

	public void inc(int idx, double val) {
		check(idx);

		arr.putScalar(idx, arr.getFloat(idx) + val);
	}

	public int length() {
		return length;
	}

	public float norml1() {
		return arr.norm1Number().floatValue();
	}

	public float norml2() {
		return arr.norm2Number().floatValue();
	}

	public Vec proj(Vec v) {
		return scale(dot(v) / dot(this));
	}

	public Vec scale(float scalar) {
		INDArray newarr = arr.mul(scalar);
		return new Vec(newarr);
	}

	public Vec div(float scalar) {
		return scale(1.0f / scalar);
	}

	public Vec sub(Vec b) {

		int bd = b.length();
		if (length != bd) {
			throw new IllegalArgumentException("Vector dimensions must be the same. This " + b + ", other " + bd + ".");
		}

		INDArray newarr = arr.sub(b.arr);

		return new Vec(newarr);
	}

	public float[] toArray() {

		final int d = length();
		float[] arr = new float[d];
		for (int j = 0; j < d; j++) {
			arr[j] = get(j);
		}

		return arr;
	}

	public Vec unit() {

		float norm = norml2();
		INDArray newarr = arr.div(norm);

		return new Vec(newarr);
	}

	public void print() {
		print(System.out, MAX_PRINT);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		print(sb, MAX_PRINT);
		return sb.toString();
	}

	public void print(Appendable out, int max) {

		max = (max < length) ? max : length;

		try {
			if (max < length) {
				out.append("Vec (" + length + ")  (truncated)\n");
			} else {
				out.append("Vec (" + length + ")\n");
			}

			NumberFormat f = DecimalFormat.getNumberInstance();
			f.setMaximumFractionDigits(4);
			f.setMinimumFractionDigits(4);
			f.setGroupingUsed(true);

			int maxLength = 0;

			for (int i = 0; i < max; i++) {

				String str = f.format(get(i));

				maxLength = Math.max(maxLength, str.length());
			}

			for (int i = 0; i < max; i++) {

				String str = f.format(get(i));
				int leading = maxLength - str.length();
				for (int s = 0; s < leading; s++) {
					out.append(" ");
				}

				out.append(str);

				out.append("\n");
			}

			out.append("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

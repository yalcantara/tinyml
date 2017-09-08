package com.tinyml.structs;

import static java.lang.Math.sqrt;

public class GridColumnInfo {

	private final int col;
	private final int count;

	private int numbers;
	private int integers;
	private int missing;

	private double max;
	private double min;
	private double sum;
	private double var;
	private double std;

	public GridColumnInfo(Grid g, int col) {
		this.col = col;
		this.count = g.rows();

		int n = 0;
		double mean = 0.0;
		double m2 = 0.0;
		for (int i = 0; i < count; i++) {

			String val = g.get(i, col);

			if (val == null) {
				missing++;
			} else {
				double parsed = 0.0;
				boolean isNum = false;
				if (isInteger(val)) {
					isNum = true;
					numbers++;
					integers++;
					parsed = parseInt(val);

				} else if (isNumber(val)) {
					isNum = true;
					numbers++;
					parsed = parseNumber(val);
				}

				if (isNum) {
					max = Math.max(max, parsed);
					min = Math.min(min, parsed);

					sum += parsed;

					// from:
					// https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
					double x = parsed;
					n += 1;
					double delta = x - mean;
					mean += delta / n;
					double delta2 = x - mean;
					m2 += delta * delta2;

				}
			}
		}

		if (numbers >= 2) {
			var = m2 / (n - 1);
			std = sqrt(var);
		} else {
			var = 0.0;
			std = sqrt(var);
		}
	}

	public int col() {
		return col;
	}

	public double sum() {
		return sum;
	}

	public double max() {
		return max;
	}

	public double min() {
		return min;
	}

	public double avg() {
		if (numbers > 0) {
			return sum / numbers;
		}
		return 0.0;
	}

	public double stdev() {
		return std;
	}

	public int integers() {
		return integers;
	}

	public int numbers() {
		return numbers;
	}

	public int missing() {
		return missing;
	}

	public int count() {
		return count;
	}

	public int words() {
		return count - numbers - missing;
	}

	public int values() {
		return count - missing;
	}

	public boolean hasMissing() {
		return missing > 0;
	}

	public boolean isEmpty() {
		return values() == 0;
	}

	public boolean isInteger() {
		return isEmpty() == false && values() == integers;
	}

	public boolean isNumeric() {
		return isEmpty() == false && values() == numbers;
	}

	public boolean isBinary() {
		return isInteger() && max == 1.0 && min == 0.0;
	}

	private boolean isNumber(String val) {
		try {
			Double.parseDouble(val);
			return true;
		} catch (Exception ex) {

		}

		return false;
	}

	private int parseInt(String val) {
		return Integer.parseInt(val);
	}

	private double parseNumber(String val) {
		return Double.parseDouble(val);
	}

	private boolean isInteger(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (Exception ex) {

		}

		return false;
	}
}

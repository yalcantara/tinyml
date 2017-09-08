package com.tinyml.structs;

public class GridInfo {

	private final GridColumnInfo[] cols;

	public GridInfo(Grid g) {

		cols = new GridColumnInfo[g.cols()];

		for (int i = 0; i < cols.length; i++) {
			cols[i] = new GridColumnInfo(g, i);
		}
	}

	public int cols() {
		return cols.length;
	}

	public GridColumnInfo info(int col) {
		return cols[col];
	}

	public boolean isNumeric(int col) {
		return cols[col].numbers() > 0;
	}

	public boolean isBinary(int col) {
		return cols[col].isBinary();
	}

	public boolean isInteger(int col) {
		return cols[col].isInteger();
	}

	public double max(int col) {
		return cols[col].max();
	}

	public double min(int col) {
		return cols[col].min();
	}

	public double avg(int col) {
		return cols[col].avg();
	}

	public double stdev(int col) {
		return cols[col].stdev();
	}
}

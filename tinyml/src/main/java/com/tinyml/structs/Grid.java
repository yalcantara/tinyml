package com.tinyml.structs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.datavec.api.records.Record;
import org.datavec.api.writable.Writable;

public class Grid {

	private static final int MAX_PRINT_ROWS = 300;
	private static final int MAX_PRINT_COLS = 300;

	private final List<List<String>> grid;

	private int rows;
	private int cols;

	public Grid() {
		grid = new ArrayList<List<String>>();
	}

	public void add(List<String> row) {
		if (row == null) {
			throw new IllegalArgumentException("The row parameter can not be null.");
		}
		grid.add(row);
		cols = Math.max(cols, row.size());
		rows++;
	}

	public int rows() {
		return rows;
	}

	public int cols() {
		return cols;
	}

	public void add(Record r) {
		List<Writable> list = r.getRecord();

		List<String> arr = new ArrayList<String>(list.size());

		for (int i = 0; i < list.size(); i++) {
			arr.add(list.get(i).toString());
		}

		add(arr);
	}

	public String get(int row, int col) {
		if (row < 0 || row >= rows) {
			throw new IndexOutOfBoundsException(
					"The row paramter is out of range. Got: " + row + ", rows: " + rows + ".");
		}

		if (col < 0 || col >= cols) {
			throw new IndexOutOfBoundsException(
					"The col paramter is out of range. Got: " + row + ", cols: " + cols + ".");
		}

		List<String> arr = grid.get(row);

		if (col < arr.size()) {
			return arr.get(col);
		}

		return null;
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

	private String format(String str) {
		if (str == null) {
			return "(null)";
		}

		if (str.equals("")) {
			return "''";
		}

		if (str.length() <= 10) {
			return "'" + str + "'";
		}

		return "'" + str.substring(0, 10) + "...'";
	}

	public void print(Appendable out, int maxRows, int maxCols) {

		maxRows = (maxRows < rows) ? maxRows : rows;
		maxCols = (maxCols < cols) ? maxCols : cols;

		try {
			if (maxRows < rows || maxCols < cols) {
				out.append("Grid " + rows + "x" + cols + "  (truncated)\n");
			} else {
				out.append("Grid " + rows + "x" + cols + "\n");
			}

			int[] maxLength = new int[maxCols];

			for (int j = 0; j < maxCols; j++) {
				for (int i = 0; i < maxRows; i++) {

					String str = format(get(i, j));

					maxLength[j] = Math.max(maxLength[j], str.length());
				}
			}

			for (int j = 0; j < maxCols; j++) {
				if (j == 0) {
					out.append("--");
				}
				if (j > 0) {
					out.append("---");
				}
				int leading = maxLength[j];
				for (int s = 0; s < leading; s++) {
					out.append("-");
				}

				if (j + 1 >= maxCols) {
					out.append("--");
				}
			}

			out.append("\n");

			for (int i = 0; i < maxRows; i++) {
				for (int j = 0; j < maxCols; j++) {
					if (j == 0) {
						out.append("| ");
					}

					if (j > 0) {
						out.append(" | ");
					}
					String str = format(get(i, j));
					int leading = maxLength[j] - str.length();
					for (int s = 0; s < leading; s++) {
						out.append(" ");
					}

					out.append(str);

					if (j + 1 >= maxCols) {
						out.append(" |");
					}
				}
				out.append("\n");
			}

			for (int j = 0; j < maxCols; j++) {
				if (j == 0) {
					out.append("--");
				}
				if (j > 0) {
					out.append("---");
				}
				int leading = maxLength[j];
				for (int s = 0; s < leading; s++) {
					out.append("-");
				}

				if (j + 1 >= maxCols) {
					out.append("--");
				}
			}

			out.append("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

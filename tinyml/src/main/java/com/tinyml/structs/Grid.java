package com.tinyml.structs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.datavec.api.records.Record;
import org.datavec.api.writable.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grid {

	private static final Logger log = LoggerFactory.getLogger(Grid.class);

	private static final int MAX_PRINT_ROWS = 300;
	private static final int MAX_PRINT_COLS = 300;

	private final List<String[]> grid;

	private int rows;
	private int cols;

	public Grid() {
		grid = new ArrayList<String[]>();
	}

	private void quickAdd(String[] row) {
		grid.add(row);
		cols = Math.max(cols, row.length);
		rows++;
	}

	public void add(List<String> row) {
		if (row == null) {
			throw new IllegalArgumentException("The row parameter can not be null.");
		}

		String[] record = new String[row.size()];
		for (int i = 0; i < record.length; i++) {
			String str = row.get(i);

			if (str == null || str.equals("")) {
				record[i] = null;
			} else {
				str = str.trim();
				if (str.equals("")) {
					log.warn("Adding a value full of spaces. Default to null. Col: " + (i + 1));
					record[i] = null;
				} else {

					record[i] = str;
				}
			}
		}

		for (int i = 0; i < record.length; i++) {
			if (record[i] != null) {
				break;
			}

			if (i + 1 >= record.length) {
				// full empty row is ignored
				log.warn("Ignoring full empty row.");
				return;
			}
		}

		quickAdd(record);
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

		String[] arr = grid.get(row);

		if (col < arr.length) {
			return arr[col];
		}

		return null;
	}

	public Grid selectCols(int start, int end) {
		int l = end - start;
		if (start < 0) {
			throw new IllegalArgumentException("The 'start' parameter must be 0 or higher. Got: " + start + ".");
		}

		if (end > cols) {
			throw new IllegalArgumentException(
					"The 'end' parameter must less or equal to the number of columns. Got: " + end + ".");
		}

		if (end <= start) {
			throw new IllegalArgumentException(
					"The 'end' parameter must be higher than the 'start' parameter. Expected >  " + start + ", but got "
							+ end + " instead.");
		}

		Grid ans = new Grid();

		for (int i = 0; i < rows; i++) {
			String[] row = new String[l];

			for (int j = 0; j < l; j++) {
				row[j] = get(i, start + j);
			}
			ans.quickAdd(row);
		}

		return ans;
	}

	public GridInfo info() {
		return new GridInfo(this);
	}

	public WordCount countWords(int col) {

		WordCount wc = new WordCount();
		for (int i = 0; i < rows; i++) {
			wc.add(get(i, col));
		}

		return wc;
	}

	public Mat toMatrix() {
		return toMatrix(false);
	}

	public Mat toMatrix(boolean stdScale) {

		GridInfo gi = info();

		Mat[] matrices = new Mat[cols];

		int n = 0;
		for (int i = 0; i < cols; i++) {
			matrices[i] = toMatrix(gi.info(i));
			n += matrices[i].cols();
		}

		Mat ans = new Mat(rows, n);

		for (int i = 0; i < rows; i++) {

			int j = 0;
			for (int c = 0; c < matrices.length; c++) {
				Mat crt = matrices[c];
				for (int d = 0; d < crt.cols(); d++) {

					float val = crt.get(i, d);

					if (stdScale && gi.isNumeric(c)) {

						if (gi.isBinary(c)) {
							ans.set(i, j + d, val);
						} else if (rows >= 2) {
							float std = (float) gi.stdev(c);

							float avg = (float) gi.avg(c);
							float nval = (val - avg) / std;
							ans.set(i, j + d, nval);

						} else {
							ans.set(i, j + d, val);
						}

					} else {

						ans.set(i, j + d, val);
					}

				}
				j += crt.cols();
			}
		}

		return ans;
	}

	private Mat toMatrix(GridColumnInfo info) {

		int col = info.col();
		int m = info.count();

		Mat a;
		if (info.isNumeric()) {

			if (info.hasMissing()) {
				a = new Mat(m, 2);
			} else {
				a = new Mat(m, 1);
			}

			for (int i = 0; i < m; i++) {
				String str = get(i, col);
				if (str == null) {
					a.set(i, 1, 1); // put it in the second col
				} else {
					a.set(i, 0, parse(str));
				}
			}
		} else {
			WordCount count = countWords(col);

			int n = count.diff();
			a = new Mat(m, n);

			for (int i = 0; i < m; i++) {
				String str = get(i, col);
				int dest = count.wordIdx(str);
				if (dest == -1) {
					throw new IllegalArgumentException("Could not find word '" + str + "' in the word cound.");
				}
				a.set(i, dest, 1);
			}
		}

		return a;
	}

	private static float parse(String val) {
		return Float.parseFloat(val);
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

		int len = 20;
		if (str.length() <= len) {
			return str;
		}

		return str.substring(0, len) + "...";
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

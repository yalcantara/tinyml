package com.tinyml.structs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.Record;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;

public class Loader {

	public static Grid csv(String path) {
		return csv(new File(path));
	}

	public static Grid csv(File f) {

		try (RecordReader rr = new CSVRecordReader(0, ',')) {

			rr.initialize(new FileSplit(f));

			Grid g = new Grid();
			main: while (rr.hasNext()) {
				Record row = rr.nextRecord();

				List<Writable> list = row.getRecord();

				for (int i = 0; i < list.size(); i++) {
					String str = list.get(i).toString();
					if (str != null && str.isEmpty() == false) {
						break;
					}

					if (i + 1 >= list.size()) {
						// at this point, all entries are null or empty
						// we have to ignore this line
						continue main;
					}

				}

				g.add(row);
			}

			return g;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}

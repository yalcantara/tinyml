package com.tinyml.structs;

import java.io.File;
import java.io.IOException;

import org.datavec.api.records.Record;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;

public class Loader {

	public static Grid csv(String path) {
		return csv(new File(path));
	}

	public static Grid csv(File f) {

		try (RecordReader rr = new CSVRecordReader(0, ',')) {

			rr.initialize(new FileSplit(f));

			Grid g = new Grid();
			while (rr.hasNext()) {
				Record row = rr.nextRecord();

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

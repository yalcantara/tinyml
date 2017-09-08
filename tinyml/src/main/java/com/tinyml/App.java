package com.tinyml;

import org.nd4j.linalg.eigen.Eigen;

import com.tinyml.structs.Grid;
import com.tinyml.structs.Loader;
import com.tinyml.structs.Mat;
import com.tinyml.structs.Vec;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		Grid g = Loader.csv("files/adult.data");
		Mat x = g.selectCols(0, 14).toMatrix(true);

		Mat covar = x.transp().dot(x);
		Mat eigen = covar.clone();

		Vec eigval = Vec.wrap(Eigen.symmetricGeneralizedEigenvalues(eigen.ptr()));

		double sum = eigval.sum();

		for (int i = 0; i < eigval.length(); i++) {
			double val = eigval.get(i);
			double perc = val / sum * 100;
			System.out.printf("%4d  %12.4f   of  %12.4f    ==>  %6.2f\n", i, val, sum, perc);
		}

	}
}

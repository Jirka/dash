package cz.vutbr.fit.dashapp.segmenation.util.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FindFrequentValuesUtil {
	
	/**
	 * Method heuristically finds the most frequently occurred colors.
	 * 
	 * @param matrix
	 * @return
	 */
	public static List<Integer> find(int[][] matrix) {
		int a = MatrixUtils.area(matrix);
		
		 // make histogram
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// find the most frequent value (possible background)
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		//System.out.println(mostFrequentValue + " " + (double) histogram[mostFrequentValue]/a);
		
		// store frequent value
		List<Integer> frequentValues = new ArrayList<>();
		frequentValues.add(mostFrequentValue);
		int sum = histogram[mostFrequentValue];
		
		// find another frequent values
		mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		while(
				(double) histogram[mostFrequentValue]/a >= 0.001 && (double) sum/a < 0.5 ||
				(double) histogram[mostFrequentValue]/a >= 0.05 && (double) sum/a < 0.6 ||
				(double) histogram[mostFrequentValue]/a >= 0.1 && (double) sum/a < 0.7
		) {
			frequentValues.add(mostFrequentValue);
			sum += histogram[mostFrequentValue];
			mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		}
		
		return frequentValues;
	}
	
	/**
	 * Method heuristically finds the most frequently occurred colors.
	 * Experimental method.
	 * 
	 * @param matrix
	 * @return
	 */
	public static List<Integer> find2(int[][] matrix) {
		int a = MatrixUtils.area(matrix);
		
		// make histogram
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// find the most frequent value (possible background)
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		//System.out.println(mostFrequentValue + " " + (double) histogram[mostFrequentValue]/a);
		
		// store frequent value
		List<Integer> frequentValues = new ArrayList<>();
		frequentValues.add(mostFrequentValue);
		int sum = histogram[mostFrequentValue];
		
		// find another frequent values
		int i = 1;
		do {
			mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
			if(
					//(double) histogram[mostFrequentValue]/a < 0.01
					!((double) histogram[mostFrequentValue]/a >= 0.001 && (double) sum/a < 0.5 ||
					(double) histogram[mostFrequentValue]/a >= 0.05 && (double) sum/a < 0.6 ||
					(double) histogram[mostFrequentValue]/a >= 0.1 && (double) sum/a < 0.7)
					//|| frequentValues.size() > 2
			) {
				break;
			}
			frequentValues.add(mostFrequentValue);
			sum += histogram[mostFrequentValue];
			i++;
		} while(i < 16); // max 16 values
		
		List<Integer> frequentValuesSort = new ArrayList<>(frequentValues);
		Collections.sort(frequentValuesSort);
		
		int j;
		int expected, act;
		List<Integer> removeValues = new ArrayList<>();
		for (Integer frequentValue : frequentValues) {
			i = frequentValuesSort.indexOf(frequentValue);
			if(i >= 0) {
				// search lower
				j = i-1;
				expected = frequentValue-1;
				while(j >= 0) {
					act = frequentValuesSort.get(j);
					if(act == expected && histogram[act] < histogram[act+1]*1.1) { // 1.1 some tolerance
						removeValues.add(act);
						expected--;
						j--;
					} else {
						break;
					}
				}
				
				// search higher
				j = i+1;
				expected = frequentValue+1;
				while(j < frequentValuesSort.size()) {
					act = frequentValuesSort.get(j);
					if(act == expected && histogram[act] < histogram[act-1]*1.1) {
						removeValues.add(act);
						expected++;
						j++;
					} else {
						break;
					}
				}
				
				for (Integer value : removeValues) {
					frequentValuesSort.remove(value);
				}
				removeValues.clear();
			}
		}
		
		List<Integer> frequentValuesFiltered = new ArrayList<>();
		for (Integer frequentValue : frequentValues) {
			if(frequentValuesSort.contains(frequentValue)) {
				frequentValuesFiltered.add(frequentValue);
			}
		}
		
		return frequentValuesFiltered;
	}

}

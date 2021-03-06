package cz.vutbr.fit.dashapp.image.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class HistogramUtils {
	
	public static int[] getGrayscaleHistogram(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int histogram[] = new int[256];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				histogram[matrix[i][j]]++;
			}
		}
		
		return histogram;
	}

	public static int[] reduceHistogram(int[] histogram, int n) {
		int size = histogram.length;
		if(n > size) {
			n = size;
		}
		int chunkSize = size/n;
		int mod = size%n;
		int[] reducedHistogram = new int[n];
		for (int i = 0; i < reducedHistogram.length; i++) {
			reducedHistogram[i] = 0;
		}
		
		int chunkI = 0;
		for (int i = 0, j = 0; i < size; i++) {
			reducedHistogram[j] += histogram[i];
			chunkI++;
			if(chunkI == chunkSize) {
				if(mod > 0) {
					i++;
					reducedHistogram[j] += histogram[i];
					mod--;
				}
				chunkI = 0;
				j++;
			}
		}
		
		return reducedHistogram;
	}
	
	public static Map<Integer, Integer> getColorHistogram(int[][] matrix) {
		Map<Integer, Integer> map = new HashMap<>();
		int histogram[] = new int[256];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		Integer val;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				val = map.get(matrix[i][j]);
				if(val == null) {
					map.put(matrix[i][j], 1);
				} else {
					map.put(matrix[i][j], val+1);
				}
			}
		}
		
		return map;
	}
	
	public static int findMax(int[] histogram, int upperLimitIndex) {
		int maxBarIndex = -1;
		int maxBarValue = -1; // we need to store value (problem with upperLimitIndex)
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] >= maxBarValue) {
				if(upperLimitIndex < 0 || histogram[i] < histogram[upperLimitIndex] || (histogram[i] == histogram[upperLimitIndex] && i < upperLimitIndex)) {
					maxBarIndex = i;
					maxBarValue = histogram[i];
				}
			}
		}
		return maxBarIndex;
	}

	public static List<Integer> getUsedValues(int[][] matrix) {
		List<Integer> usedValues = new ArrayList<>();
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] > 0) {
				usedValues.add(i);
			}
		}
		
		return usedValues;
	}

}

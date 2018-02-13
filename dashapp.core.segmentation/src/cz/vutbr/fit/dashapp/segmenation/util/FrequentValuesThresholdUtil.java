package cz.vutbr.fit.dashapp.segmenation.util;

import java.util.Collections;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class FrequentValuesThresholdUtil {
	
	/**
	 * 
	 * @param matrix
	 * @param frequentValues
	 * @return
	 */
	public static int[][] threshold(int[][] matrix, List<Integer> frequentValues) {
		final int w = MatrixUtils.width(matrix);
		final int h = MatrixUtils.height(matrix);
		double actColor = 225.0; // white color (255.0) is not appropriate for further debug purposes
		double colorInterval = actColor/frequentValues.size();
		int[][] frequentColorMatrix = new int[w][h];
		GrayMatrix.clearMatrix(frequentColorMatrix, GrayMatrix.BLACK);
		for (Integer frequentValue : frequentValues) {
			GrayMatrix.copyPixels(frequentColorMatrix, matrix, frequentValue, (int) actColor);
			actColor-=colorInterval;
		}
		
		//int colorAmount=frequentValues.size()+1;
		//debug("sort/" + (colorAmount > 4 ? "x" : colorAmount) + "/histogram_70", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		
		return frequentColorMatrix;
	}
	
	public static int[][] threshold2(int[][] matrix, List<Integer> frequentValues) {
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		Collections.sort(frequentValues);
		int actColor, previousColor = 0, nextColor = 256, start, end;
		int[][] frequentColorMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.BLACK);
		if(!frequentValues.isEmpty()) {
			int firstValue = frequentValues.get(0);
			if(firstValue > 20) {
				frequentValues.add(0,0);
			}
			
			int lastValue = frequentValues.get(frequentValues.size()-1);
			if(lastValue < 235) {
				frequentValues.add(255);
			}
			
			int i_last = frequentValues.size()-1;
			actColor = frequentValues.get(0);
			for (int i = 0; i <= i_last; i++) {
				if(i == 0) {
					start = actColor;
				} else {
					start = (actColor+previousColor)/2;
				}
				
				if(i == i_last) {
					end = 256;
				} else {
					nextColor = frequentValues.get(i+1);
					end = (actColor+nextColor)/2;
				}
				
				for (int x = 0; x < mW; x++) {
					for (int y = 0; y < mH; y++) {
						if(matrix[x][y] >= start && matrix[x][y] < end) {
							frequentColorMatrix[x][y] = actColor;
						}
					}
				}
				//GrayMatrix.copyPixels(frequentColorMatrix, matrix, actColor, (int) actColor);
				previousColor = actColor;
				actColor = nextColor;
			}
		}
		
		// set colors according to frequency of color occurrence
		int[] histogram = HistogramUtils.getGrayscaleHistogram(frequentColorMatrix);
		frequentValues.clear();
		
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1); // find next frequent value
		do {
			frequentValues.add(mostFrequentValue);
			previousColor = mostFrequentValue;
			mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		} while(histogram[mostFrequentValue] != 0);
		
		frequentColorMatrix = threshold(frequentColorMatrix, frequentValues);
		
		//int colorAmount=frequentValues.size()+1;
		//debug("sort/" + (colorAmount > 4 ? "x" : colorAmount) + "/histogram_70", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		
		return frequentColorMatrix;
	}

}

package cz.vutbr.fit.dashapp.eval.metric;

import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

public class ColorShare extends RasterMetric implements IMetric {
	
	protected int posterization = -1;
	
	public ColorShare(Dashboard dashboard, int[][] matrix) {
		super(dashboard, matrix);
	}
	
	public ColorShare(Dashboard dashboard, int[][] matrix, int posterization) {
		super(dashboard, matrix);
		setPosterization(posterization);
	}
	
	public void setPosterization(int posterization) {
		this.posterization = posterization;
	}

	@Override
	public String getInicials() {
		return "CLR";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "count", "% (posterization)", "%", "% 1.st", "1.st clr", "% 2.nd", "2.nd clr", "% 1.+2." };
	}
	
	public static Map.Entry<Integer, Integer> getMax(Map<Integer, Integer> histogram, Map.Entry<Integer, Integer> limit) {
		Map.Entry<Integer, Integer> max = null;
		for (Map.Entry<Integer, Integer> item : histogram.entrySet()) {
			if(max != null) {
				if(item.getValue() > max.getValue()) {
					if(limit == null || (item.getValue() <= limit.getValue() && item != limit)) {
						max = item;
					}
				}
			} else if(limit == null || (item != limit && item.getValue() <= limit.getValue())) {
				max = item;
			}
		}
		return max;
	}
	
	public Object measure() {
		int[][] posterizedMatrix = matrix;
		if(posterization > 0) {
			posterizedMatrix = MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, posterization)), true); // 4 bit
		}
		Map<Integer, Integer> histogram = MatrixUtils.getColorHistogram(posterizedMatrix);
		long count = histogram.values().size();
		double posterizationShare = (((double) count)/(posterization > 0 ? Math.pow(2, posterization*3) : 16777216))*100;
		double share = (((double) count)/16777216)*100; //2^(8*3)
		
		Map.Entry<Integer, Integer> max = getMax(histogram, null);
		Integer key = max.getKey();
		Integer color1Count = max.getValue();
		int area = posterizedMatrix.length*posterizedMatrix[0].length;
		String color1 = "(" + MatrixUtils.getRed(key) + "," + MatrixUtils.getGreen(key) + "," + MatrixUtils.getBlue(key) + ")";
		double color1Share = ((((double) color1Count)/(area))*100);
		
		max = getMax(histogram, max);
		key = max.getKey();
		Integer color2Count = max.getValue();
		String color2 = "(" + MatrixUtils.getRed(key) + "," + MatrixUtils.getGreen(key) + "," + MatrixUtils.getBlue(key) + ")";
		double color2Share = ((((double) color2Count)/(area))*100);
		return new Object[] { count, posterizationShare, share, color1Share, color1, color2Share, color2, color1Share+color2Share };
	}
	
	public Object measure(int posterization) {
		setPosterization(posterization);
		return measure();
	}

}

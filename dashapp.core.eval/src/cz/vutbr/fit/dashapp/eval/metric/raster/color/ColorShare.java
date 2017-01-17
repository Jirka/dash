package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import java.util.Map;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

public class ColorShare implements IMetric {
	
	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		// TODO Auto-generated method stub
		return null;
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
	
	public MetricResult[] measure(int[][] matrix) {
		return measure(matrix, -1);
	}
	
	public MetricResult[] measure(int[][] matrix, int posterization) {
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
		return new MetricResult[] {
				new MetricResult("Color Count", "CLR_n", count),
				new MetricResult("Color Share (posterized)", "% CLR_p", posterizationShare),
				new MetricResult("Color Share", "% CLR", share),
				new MetricResult("1st Color Count", "% CLR_1", color1Share),
				new MetricResult("1st Color", "CLR_1", color1),
				new MetricResult("2nd Color Share", "% CLR_2", color2Share),
				new MetricResult("2nd Color", "CLR_2", color2),
				new MetricResult("1+2 Color Share", "% CLR_1_2", color1Share+color2Share)
		};
	}
}

package cz.vutbr.fit.dashapp.eval.metric.raster;

import java.util.Map;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ColorShare extends AbstractRasterMetric implements IRasterMetric {
	
	private int posterizationBitValue = -1;
	
	public ColorShare() {
		super();
	}
	
	public ColorShare(int posterizationBitValue) {
		super();
		setPosterizationBitValue(posterizationBitValue);
	}
	
	public ColorShare setPosterizationBitValue(int posterizationBitValue) {
		this.posterizationBitValue = posterizationBitValue;
		return this;
	}
	
	public int getPosterizationBitValue() {
		return posterizationBitValue;
	}
	
	private Map.Entry<Integer, Integer> getMax(Map<Integer, Integer> histogram, Map.Entry<Integer, Integer> limit) {
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
		int posterization = getPosterizationBitValue();
		int[][] posterizedMatrix = matrix;
		if(posterization > 0) {
			posterizedMatrix = PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, posterization)), true); // 4 bit
		}
		Map<Integer, Integer> histogram = HistogramUtils.getColorHistogram(posterizedMatrix);
		long count = histogram.values().size();
		double posterizationShare = (((double) count)/(posterization > 0 ? Math.pow(2, posterization*3) : 16777216))*100;
		double share = (((double) count)/16777216)*100; //2^(8*3)
		
		Map.Entry<Integer, Integer> max = getMax(histogram, null);
		Integer key = max.getKey();
		Integer color1Count = max.getValue();
		int area = MatrixUtils.area(posterizedMatrix);
		String color1 = "(" + ColorMatrix.getRed(key) + "," + ColorMatrix.getGreen(key) + "," + ColorMatrix.getBlue(key) + ")";
		double color1Share = ((((double) color1Count)/(area))*100);
		
		max = getMax(histogram, max);
		key = max.getKey();
		Integer color2Count = max.getValue();
		String color2 = "(" + ColorMatrix.getRed(key) + "," + ColorMatrix.getGreen(key) + "," + ColorMatrix.getBlue(key) + ")";
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

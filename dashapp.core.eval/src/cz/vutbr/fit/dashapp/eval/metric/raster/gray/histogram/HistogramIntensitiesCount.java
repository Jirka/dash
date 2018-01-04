package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HistogramIntensitiesCount extends AbstractGrayHistogramMetric {
	
	private int calculateIntensities(int[] histogram, int limit) {
		int count = 0;
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] > 0) {
				if(limit < 0 || histogram[i] >= limit) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public MetricResult[] measureGrayHistogram(int[] histogram) {
		int area = getArea(histogram);
		int count = calculateIntensities(histogram, -1);
		int reducedCount = calculateIntensities(histogram, (int) (area*0.1));
		int reducedCount2 = calculateIntensities(histogram, (int) (area*0.05));
		int reducedCount3 = calculateIntensities(histogram, (int) (area*0.01));
		int reducedCount4 = calculateIntensities(histogram, (int) (area*0.005));
		int reducedCount5 = calculateIntensities(histogram, (int) (area*0.001));
		return new MetricResult[] {
				new MetricResult("Intensities count", "IC", count),
				new MetricResult("Intensities count (% clr used)", "IC_share", (((double) count)/256)*100),
				new MetricResult("Intensities count (10%)", "IC_10", reducedCount),
				new MetricResult("Intensities count (5%)", "IC_5", reducedCount2),
				new MetricResult("Intensities count (1%)", "IC_1", reducedCount3),
				new MetricResult("Intensities count (0.5%)", "IC_0.5", reducedCount4),
				new MetricResult("Intensities count (0.1%)", "IC_0.1", reducedCount5) 
		};
	}
}

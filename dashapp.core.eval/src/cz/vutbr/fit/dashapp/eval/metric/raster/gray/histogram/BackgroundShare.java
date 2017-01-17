package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

public class BackgroundShare extends AbstractGrayHistogramMetric {
	
	private int findMax(int[] histogram, int upperLimitIndex) {
		int max = 0;
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] > histogram[max]) {
				if(upperLimitIndex < 0 || histogram[i] < histogram[upperLimitIndex]) {
					max = i;
				}
			}
		}
		return max;
	}

	@Override
	public MetricResult[] measureGrayHistogram(int[] histogram) {
		int max = findMax(histogram, -1);
		int secondMax = findMax(histogram, max);
		int pixelCount = getArea(histogram);
		double first = ((double) histogram[max])/pixelCount*100;
		double second = ((double) histogram[secondMax])/pixelCount*100;
		return new MetricResult[] {
				new MetricResult("Background Share (1+2)", "BGS", first+second),
				new MetricResult("Background Share (1st)", "BGS", first),
				new MetricResult("Background Share (2nd)", "BGS", second)
		};
	}
}

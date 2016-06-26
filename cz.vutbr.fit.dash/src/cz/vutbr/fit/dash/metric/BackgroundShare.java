package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class BackgroundShare extends HistogramMetric implements IMetric {

	public BackgroundShare(Dashboard dashboard, int[] histogram) {
		super(dashboard, histogram);
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "1+2", "1st", "2nd" };
	}

	@Override
	public String getInicials() {
		return "BGS";
	}
	
	private int findMax(int upperLimitIndex) {
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
	public Object measure() {
		int max = findMax(-1);
		int secondMax = findMax(max);
		int pixelCount = dashboard.area();
		double first = ((double) histogram[max])/pixelCount*100;
		double second = ((double) histogram[secondMax])/pixelCount*100;
		return new Object[] { first+second, first, second };
	}
}

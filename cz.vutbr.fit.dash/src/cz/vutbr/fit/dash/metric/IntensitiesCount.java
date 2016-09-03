package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public class IntensitiesCount extends HistogramMetric implements IMetric {

	public IntensitiesCount(Dashboard dashboard, int[] histogram) {
		super(dashboard, histogram);
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "% clr used", "10%", "5%", "1%", "0.5%", "0.1" };
	}

	@Override
	public String getInicials() {
		return "IC";
	}
	
	private int calculateIntensities(int limit) {
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
	public Object measure() {
		int area = getArea();
		int count = calculateIntensities(-1);
		int reducedCount = calculateIntensities((int) (area*0.1));
		int reducedCount2 = calculateIntensities((int) (area*0.05));
		int reducedCount3 = calculateIntensities((int) (area*0.01));
		int reducedCount4 = calculateIntensities((int) (area*0.005));
		int reducedCount5 = calculateIntensities((int) (area*0.001));
		return new Object[] { count, (((double) count)/256)*100, reducedCount, reducedCount2, reducedCount3,
				reducedCount4, reducedCount5 };
	}
}

package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;

public class MetricResultsCollection {
	
	public MetricResult[][] results;
	
	public MetricResultsCollection(int length) {
		results = new MetricResult[length][];
	}
	
	private double meanValue(int statID) {
		double mean = 0;
		int size = results.length;
		
		for (int i = 0; i < size; i++) {
			mean += results[i][statID].number();
		}
		mean = mean/(size);
		
		return mean;
	}

	private double varianceValue(int statID, double mean) {
		double variance = 0.0;
		int size = results.length;
		double act;
		
		for (int i = 0; i < size; i++) {
			act = (double) results[i][statID].number();
			variance += (mean-act)*(mean-act);
		}
		variance = variance/(size);
		
		return variance;
	}
	
	private double minValue(int statID) {
		double min = Double.MAX_VALUE;
		int size = results.length;
		double act;
		
		for (int i = 0; i < size; i++) {
			act = (double) results[i][statID].number();
			if(min > act) {
				min = act;
			}
		}
		
		return min;
	}
	
	private double maxValue(int statID) {
		double max = Double.MIN_VALUE;
		int size = results.length;
		double act;
		
		for (int i = 0; i < size; i++) {
			act = (double) results[i][statID].number();
			if(max < act) {
				max = act;
			}
		}
		
		return max;
	}
	
	public double stdevValue(int statID, double variance) {
		return Math.sqrt(variance);
	}
	
	public MeanSatistics[] meanStatistics() {
		MeanSatistics[] statistics = null;
		int resultsLength = resultsLength();
		if(resultsLength > 0) {
			statistics = new MeanSatistics[resultsLength];
			for (int i = 0; i < statistics.length; i++) {
				statistics[i] = new MeanSatistics();
				statistics[i].mean = meanValue(i);
				statistics[i].variance = varianceValue(i, statistics[i].mean);
				statistics[i].stdev = stdevValue(i, statistics[i].variance);
				statistics[i].min = minValue(i);
				statistics[i].max = maxValue(i);
			}
		}
		return statistics;
	}

	private int resultsLength() {
		int dashLength = results.length;
		if(dashLength > 0) {
			MetricResult[] firstResults = results[0];
			int resultLength = firstResults.length;
			for (int i = 0; i < dashLength; i++) {
				MetricResult[] actResults = results[i];
				if(actResults.length == resultLength) {
					for (int j = 0; j < resultLength; j++) {
						if(!firstResults[j].isSameKind(actResults[j]) || 
								!(actResults[j].value instanceof Double ||
								actResults[j].value instanceof Integer)) {
							return -1;
						}
					}
				}	
			}
			return resultLength;
		}
		return -1;
	}

}

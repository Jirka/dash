package cz.vutbr.fit.dashapp.eval.util;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

/**
 * Class which stores multiple results calculated by metric calculator.
 * 
 * @author Jiri Hynek
 *
 */
public class MetricResultsCollection {
	
	public MetricResult[][] results;
	
	public MetricResultsCollection(int length) {
		results = new MetricResult[length][];
	}
	
	private double meanValue(int statID) {
		double mean = 0;
		int size = results.length;
		
		int usedSize = 0;
		for (int i = 0; i < size; i++) {
			if(!results[i][statID].disabled) {
				mean += results[i][statID].number();
				usedSize++;
			}
		}
		mean = mean/(usedSize);
		
		return mean;
	}

	private double varianceValue(int statID, double mean) {
		double variance = 0.0;
		int size = results.length;
		double act;
		
		int usedSize = 0;
		for (int i = 0; i < size; i++) {
			if(!results[i][statID].disabled) {
				act = (double) results[i][statID].number();
				variance += (mean-act)*(mean-act);
				usedSize++;
			}
		}
		variance = variance/(usedSize);
		
		return variance;
	}
	
	private void disableMostExtreme(int statID, double mean) {
		int size = results.length;
		double act;
		double maxDifference = 0;
		int maxDifferenceIndex = -1;
		
		for (int i = 0; i < size; i++) {
			if(!results[i][statID].disabled) {
				act = Math.abs((mean-(double) results[i][statID].number()));
				if(act > maxDifference) {
					maxDifference = act;
					maxDifferenceIndex = i;
				}
			}
		}
		
		if(maxDifferenceIndex != -1) {
			results[maxDifferenceIndex][statID].disabled = true;
		}
		
		return;
	}
	
	private double minValue(int statID) {
		double min = Double.MAX_VALUE;
		int size = results.length;
		double act;
		
		for (int i = 0; i < size; i++) {
			if(!results[i][statID].disabled) {
				act = (double) results[i][statID].number();
				if(min > act) {
					min = act;
				}
			}
		}
		
		return min;
	}
	
	private double maxValue(int statID) {
		double max = Double.MIN_VALUE;
		int size = results.length;
		double act;
		
		for (int i = 0; i < size; i++) {
			if(!results[i][statID].disabled) {
				act = (double) results[i][statID].number();
				if(max < act) {
					max = act;
				}
			}
		}
		
		return max;
	}
	
	public double stdevValue(int statID, double variance) {
		return Math.sqrt(variance);
	}
	
	public MeanStatistics[] meanStatistics(int filterExtremeItems) {		
		MeanStatistics[] statistics = null;
		int resultsLength = resultsLength();
		if(resultsLength > 0) {
			statistics = new MeanStatistics[resultsLength];
			for (int i = 0; i < statistics.length; i++) {
				statistics[i] = new MeanStatistics();
				statistics[i].mean = meanValue(i);				
				for (int j = 0; j < filterExtremeItems; j++) {
					disableMostExtreme(i, statistics[i].mean);
					statistics[i].mean = meanValue(i);
				}
				
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

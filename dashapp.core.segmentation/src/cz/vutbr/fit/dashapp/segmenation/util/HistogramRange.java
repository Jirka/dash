package cz.vutbr.fit.dashapp.segmenation.util;

import java.util.ArrayList;
import java.util.List;

public class HistogramRange implements Comparable<HistogramRange> {
	
	public HistogramPoint start;
	public HistogramPoint end;
	public HistogramPoint[] points;
	
	public HistogramRange(HistogramPoint start, HistogramPoint end) {
		this.start = start;
		this.end = end;
	}

	public boolean containsBoth(HistogramPoint histogramPoint, HistogramPoint histogramPoint2) {
		boolean found1 = false;
		boolean found2 = false;
		for (HistogramPoint point : points) {
			if(point == histogramPoint) {
				found1 = true;
			}
			if(point == histogramPoint2) {
				found2 = true;
			}
		}
		return found1 && found2;
	}

	public void addPoints(HistogramPoint[] histogramPoints, int start, int end) {
		points = new HistogramPoint[end-start+1];
		for (int i = start; i <= end; i++) {
			points[i-start] = histogramPoints[i];
		}
	}
	
	public double[] getDiffVector() {
		double[] vector = new double[points.length-1];
		for (int i = 1; i < points.length; i++) {
			vector[i-1] = (double) points[i].value()/points[i-1].value();
		}
		return vector;
	}

	public int size() {
		return end.i-start.i+1;
	}
	
	public int maxPointWeight() {
		return size()*maxPoint().type;
	}
	
	public int allPointsWeight() {
		int sum = 0;
		for (HistogramPoint point : points) {
			sum += point.type;
		}
		return sum;
	}
	
	@Override
	public String toString() {
		return start + " -> " + end;
	}

	@Override
	public int compareTo(HistogramRange range) {
		return size()-range.size();
	}
	
	public static List<HistogramRange> getRanges(HistogramPoint[] histogramPoints) {
		List<HistogramRange> ranges = new ArrayList<>();
		
		int start = -1;
		HistogramPoint previous = null;
		for (int i = 0; i < histogramPoints.length; i++) {
			HistogramPoint histogramPoint = histogramPoints[i];
			if(histogramPoint.isAfter(previous)) {
				if(start < 0) {
					start = i-1;
				}
			} else {
				if(start >= 0) {
					HistogramRange range = new HistogramRange(histogramPoints[start], previous);
					range.addPoints(histogramPoints, start, i-1);
					ranges.add(range);
					start = -1;
				}
			}
			previous = histogramPoint;
		}
		if(start >= 0) {
			HistogramRange range = new HistogramRange(histogramPoints[start], previous);
			range.addPoints(histogramPoints, start, histogramPoints.length-1);
			ranges.add(range);
		}
		return ranges;
	}

	public HistogramPoint maxPoint() {
		HistogramPoint maxPoint = points[0];
		for (int i = 1; i < points.length; i++) {
			if(points[i].value() > maxPoint.value()) {
				maxPoint = points[i];
			}
			
		}
		return maxPoint;
	}
}
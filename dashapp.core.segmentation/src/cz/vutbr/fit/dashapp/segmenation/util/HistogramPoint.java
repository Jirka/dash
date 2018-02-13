package cz.vutbr.fit.dashapp.segmenation.util;

public class HistogramPoint implements Comparable<HistogramPoint> {
	public static final int UNKNOWN = 0;
	public static final int BIG = 3;
	public static final int MEDIUM = 2;
	public static final int SMALL = 1;
	
	public int i;
	public int[] histogram;
	public int type;
	
	public HistogramPoint(int[] histogram, int i, int type) {
		this.histogram = histogram;
		this.i = i;
		this.type = type;
	}
	
	public boolean isAfter(HistogramPoint previous) {
		return previous != null && previous.i == i-1;
	}

	public int value() {
		return histogram[i];
	}

	@Override
	public int compareTo(HistogramPoint point) {
		return this.value()-point.value();
	}
	
	public boolean isLocalMax() {
		boolean isMax = true;
		
		// check left side
		if(i-1 >= 0) {
			isMax = histogram[i-1] < histogram[i];
		}
		
		// check right side
		if(i+1 < histogram.length) {
			isMax = isMax && histogram[i+1] < histogram[i];
		}
		
		return isMax;
	}
	
	@Override
	public String toString() {
		return Integer.toString(i) + "(" + type + ")";
	}
}
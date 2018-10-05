package cz.vutbr.fit.dashapp.segmenation.util.image;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents range of histogram bars which is used for analysis of histograms.
 * 
 * For instance, we can use it when we are interested in some part of histogram
 * representing significant histogram bars.
 * 
 * @author Jiri Hynek
 *
 */
public class HistogramRange implements Comparable<HistogramRange> {
	
	public HistogramBar start;
	public HistogramBar end;
	public HistogramBar[] bars;
	
	public HistogramRange(HistogramBar start, HistogramBar end) {
		this.start = start;
		this.end = end;
	}

	public boolean containsBoth(HistogramBar histogramBar, HistogramBar histogramBar2) {
		boolean found1 = false;
		boolean found2 = false;
		for (HistogramBar bar : bars) {
			if(bar == histogramBar) {
				found1 = true;
			}
			if(bar == histogramBar2) {
				found2 = true;
			}
		}
		return found1 && found2;
	}

	public void addBars(HistogramBar[] histogramBars, int start, int end) {
		bars = new HistogramBar[end-start+1];
		for (int i = start; i <= end; i++) {
			bars[i-start] = histogramBars[i];
		}
	}
	
	public double[] getDiffVector() {
		double[] vector = new double[bars.length-1];
		for (int i = 1; i < bars.length; i++) {
			vector[i-1] = (double) bars[i].value()/bars[i-1].value();
		}
		return vector;
	}

	public int size() {
		return end.i-start.i+1;
	}
	
	public int maxBarWeight() {
		return size()*maxBar().type;
	}
	
	public int allBarsWeight() {
		int sum = 0;
		for (HistogramBar bar : bars) {
			sum += bar.type;
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
	
	public static List<HistogramRange> getRanges(HistogramBar[] histogramBars) {
		List<HistogramRange> ranges = new ArrayList<>();
		
		int start = -1;
		HistogramBar previous = null;
		for (int i = 0; i < histogramBars.length; i++) {
			HistogramBar histogramBar = histogramBars[i];
			if(histogramBar.isAfter(previous)) {
				if(start < 0) {
					start = i-1;
				}
			} else {
				if(start >= 0) {
					HistogramRange range = new HistogramRange(histogramBars[start], previous);
					range.addBars(histogramBars, start, i-1);
					ranges.add(range);
					start = -1;
				}
			}
			previous = histogramBar;
		}
		if(start >= 0) {
			HistogramRange range = new HistogramRange(histogramBars[start], previous);
			range.addBars(histogramBars, start, histogramBars.length-1);
			ranges.add(range);
		}
		return ranges;
	}

	public HistogramBar maxBar() {
		HistogramBar maxBar = bars[0];
		for (int i = 1; i < bars.length; i++) {
			if(bars[i].value() > maxBar.value()) {
				maxBar = bars[i];
			}
			
		}
		return maxBar;
	}
}
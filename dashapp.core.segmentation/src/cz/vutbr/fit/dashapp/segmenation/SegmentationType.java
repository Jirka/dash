package cz.vutbr.fit.dashapp.segmenation;

import cz.vutbr.fit.dashapp.segmenation.methods.DashboardSegmentation;

/**
 * Enumeration of segmentation algorithms which can be used for analysis.
 * 
 * @author Jiri Hynek
 *
 */
public enum SegmentationType {
	DashboardSegmentation
	;
	
	String label = null;
	
	private SegmentationType() {
		this.label = "";
	}
	
	private SegmentationType(String label) {
		this.label = label;
	}
	
	/**
	 * Factory.
	 * 
	 * @return
	 */
	public ISegmentationAlgorithm createAlgorithm() {
		switch (this) {
		
			case DashboardSegmentation: return new DashboardSegmentation();
			
			default: return null;
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
			case DashboardSegmentation:
				return this.name()/* + "()"*/;
	
			default:
				return super.toString();
		}
	}
}
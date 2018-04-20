package cz.vutbr.fit.dashapp.segmenation;

import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm.DebugMode;

public enum SegmentationType {
	// raster color
	XYCutFinal
	;
	
	String label = null;
	
	private SegmentationType() {
		this.label = "";
	}
	
	private SegmentationType(String label) {
		this.label = label;
	}
	
	public ISegmentationAlgorithm createAlgorithm() {
		switch (this) {
		
			// raster color
			case XYCutFinal: return new XYCutFinal(DebugMode.NONE);
			
			default: return null;
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		// raster color
		case XYCutFinal:
			return this.name()/* + "()"*/;

		default:
			return super.toString();
		}
	}
}
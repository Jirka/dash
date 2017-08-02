package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;

public abstract class AbstractWidgetRasterMetric extends AbstractWidgetMetric {
	
	protected RasterRatioCalculator ratioCalculator;

	public AbstractWidgetRasterMetric(RasterRatioCalculator ratioCalculator) {
		super();
		this.ratioCalculator = ratioCalculator;
	}
	
	@Override
	public String getName() {
		return super.getName() + "_" + ratioCalculator.getClass().getSimpleName();
	}

}

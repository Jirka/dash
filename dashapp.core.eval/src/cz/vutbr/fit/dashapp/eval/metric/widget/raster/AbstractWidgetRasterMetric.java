package cz.vutbr.fit.dashapp.eval.metric.widget.raster;

import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator.DummyRatioCalculator;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * Abstract implementation of widget raster metric.
 * 
 * It stores raster ratio calculator.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractWidgetRasterMetric extends AbstractWidgetMetric implements IWidgetRasterMetric {
	
	protected RasterRatioCalculator ratioCalculator;
	
	public AbstractWidgetRasterMetric() {
		this(GEType.ALL_TYPES, new DummyRatioCalculator());
	}
	
	public AbstractWidgetRasterMetric(GEType[] geTypes) {
		this(geTypes, new DummyRatioCalculator());
	}

	public AbstractWidgetRasterMetric(RasterRatioCalculator ratioCalculator) {
		this(GEType.ALL_TYPES, ratioCalculator);
	}
	
	public AbstractWidgetRasterMetric(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes);
		setRatioCalculator(ratioCalculator);
	}
	
	@Override
	public IWidgetRasterMetric setRatioCalculator(RasterRatioCalculator ratioCalculator) {
		this.ratioCalculator = ratioCalculator;
		return this;
	}
	
	@Override
	public RasterRatioCalculator getRatioCalculator() {
		return ratioCalculator;
	}
	
	@Override
	public String getName() {
		return super.getName() + "_" + ratioCalculator.getClass().getSimpleName();
	}

}

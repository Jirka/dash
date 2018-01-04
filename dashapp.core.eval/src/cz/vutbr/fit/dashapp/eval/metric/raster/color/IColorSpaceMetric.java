package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.IRasterMetric;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;

/**
 * Set of metrics which work with matrix represented in particular color space.
 * 
 * @author Jiri Hynek
 *
 */
public interface IColorSpaceMetric extends IRasterMetric {

	MetricResult[] measure(ColorSpace[][] matrix);

}

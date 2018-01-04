package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.AbstractRasterMetric;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpaceUtils;
import cz.vutbr.fit.dashapp.image.colorspace.RGB;

/**
 * 
 * Abstract implementation of metric which works with matrix represented in particular color space.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractColorSpaceMetric extends AbstractRasterMetric implements IColorSpaceMetric {
	
	public static final Class<?> DEFAULT_COLOR_SPACE = RGB.class;
	
	private Class<?> colorSpaceClass;
	
	public AbstractColorSpaceMetric() {
		setColorSpaceClass(DEFAULT_COLOR_SPACE);
	}

	public AbstractColorSpaceMetric(Class<?> colorSpaceClass) {
		setColorSpaceClass(colorSpaceClass);
	}
	
	public AbstractColorSpaceMetric setColorSpaceClass(Class<?> colorSpaceClass) {
		if(colorSpaceClass == null || !colorSpaceClass.isInstance(ColorSpace.class)) {
			this.colorSpaceClass = DEFAULT_COLOR_SPACE;
		} else {
			this.colorSpaceClass = colorSpaceClass;
		}
		return this;
	}
	
	public Class<?> getColorSpaceClass() {
		return colorSpaceClass;
	}

	@Override
	public MetricResult[] measure(int matrix[][]) {
		// convert to values in particular color space
		ColorSpace[][] colorSpaceMatrix = ColorSpaceUtils.fromRGB(matrix, getColorSpaceClass());
		return measure(colorSpaceMatrix);
	}

	@Override
	public abstract MetricResult[] measure(ColorSpace[][] matrix);

}

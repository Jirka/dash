package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpaceUtils;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class Colorfulness extends AbstractColorSpaceMetric implements IColorSpaceMetric {
	
	private int colorChannel;
	
	public Colorfulness() {
		super();
		setColorChannel(0);
	}
	
	public Colorfulness(int colorChannel) {
		super();
		setColorChannel(colorChannel);
	}
	
	public Colorfulness(Class<?> colorSpaceClass, int colorChannel) {
		super(colorSpaceClass);
		setColorChannel(colorChannel);
	}
	
	public Colorfulness setColorChannel(int colorChannel) {
		this.colorChannel = colorChannel;
		return this;
	}
	
	public int getColorChannel() {
		return colorChannel;
	}

	public MetricResult[] measure(ColorSpace[][] matrix) {
		int colorChannel = getColorChannel();
		double mean = ColorSpaceUtils.getColorChannelMean(matrix, colorChannel);
		double stdDev = ColorSpaceUtils.getColorChannelStdDev(matrix, mean, colorChannel);
		return new MetricResult[] {
				new MetricResult("Colorfulness (m+s)", "CLR", mean+stdDev),
				new MetricResult("Colorfulness m", "CLR_m", mean),
				new MetricResult("Colorfulness s", "CLR_s", stdDev)
		};
	}
}

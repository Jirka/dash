package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;

public class Colorfulness extends AbstractMetric implements IMetric {
	
	protected ColorChannel[][] matrix;
	protected ColorChannelType type;
	
	public Colorfulness(Dashboard dashboard, ColorChannel[][] matrix, ColorChannelType type) {
		super(dashboard, GEType.ALL_TYPES);
		this.matrix = matrix;
		setType(type);
	}
	
	public void setType(ColorChannelType type) {
		this.type = type;
	}

	@Override
	public String getInicials() {
		return "CLR";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "mean", "std dev" };
	}

	@Override
	public Object measure() {
		double mean = MatrixUtils.getColorChannelMean(matrix, type);
		double stdDev = MatrixUtils.getColorChannelStdDev(matrix, mean, type);
		return new Object[] { mean+stdDev, mean, stdDev };
	}
	
	public Object measure(ColorChannelType type) {
		setType(type);
		return measure();
	}

}

package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public abstract class RasterMetric extends AbstractMetric implements IMetric {
	
	protected int[][] matrix;

	public RasterMetric(Dashboard dashboard, int[][] matrix) {
		super(dashboard);
		this.matrix = matrix;
	}

}

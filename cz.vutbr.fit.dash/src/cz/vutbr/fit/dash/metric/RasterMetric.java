package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public abstract class RasterMetric extends AbstractMetric implements IMetric {
	
	protected int[][] matrix;

	public RasterMetric(Dashboard dashboard, int[][] matrix) {
		super(dashboard, Type.ALL_TYPES);
		this.matrix = matrix;
	}

}

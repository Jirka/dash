package cz.vutbr.fit.dash.eval.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public abstract class RasterMetric extends AbstractMetric implements IMetric {
	
	protected int[][] matrix;

	public RasterMetric(Dashboard dashboard, int[][] matrix) {
		super(dashboard, GEType.ALL_TYPES);
		this.matrix = matrix;
	}

}

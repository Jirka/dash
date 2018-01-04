package cz.vutbr.fit.dashapp.eval.metric.widget.basic;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;

/**
 * Screen area used by widgets.
 * 
 * @author Jiri Hynek
 *
 */
public class WidgetUsedArea extends AbstractWidgetMetric {
	
	public WidgetUsedArea() {
		super();
	}
	
	public WidgetUsedArea(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		boolean matrix[][] = BooleanMatrix.printDashboard(dashboard, true, getGeTypes());
		int area = 0;
		for (int i = 0; i < dashboard.width; i++) {
			for (int j = 0; j < dashboard.height; j++) {
				if(matrix[i][j]) {
					area++;
				}
			}			
		}
		
		return new MetricResult[] {
				new MetricResult("Area", "A", ((double) area)/dashboard.area())
		};
	}

}

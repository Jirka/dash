package cz.vutbr.fit.dashapp.eval.metric.widget.basic;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;

public class Area extends AbstractWidgetMetric {

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		// calculate width and height of layout
		/*int area = 0;
		List<GraphicalElement> elements = dashboard.getChildren(types);
		for (GraphicalElement ge : elements) {
			area += ge.area();
		}*/
		boolean matrix[][] = BooleanMatrix.printDashboard(dashboard, true, types);
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

package cz.vutbr.fit.dashapp.eval.analysis;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.eval.metric.Cohesion;
import cz.vutbr.fit.dashapp.eval.metric.Density;
import cz.vutbr.fit.dashapp.eval.metric.Economy;
import cz.vutbr.fit.dashapp.eval.metric.Equilibrium;
import cz.vutbr.fit.dashapp.eval.metric.Homogenity;
import cz.vutbr.fit.dashapp.eval.metric.NgoBalance;
import cz.vutbr.fit.dashapp.eval.metric.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.Proportion;
import cz.vutbr.fit.dashapp.eval.metric.Regularity;
import cz.vutbr.fit.dashapp.eval.metric.Sequence;
import cz.vutbr.fit.dashapp.eval.metric.Simplicity;
import cz.vutbr.fit.dashapp.eval.metric.Unity;

public class WidgetAnalysis extends AbstractAnalysis implements IAnalysis {

	public WidgetAnalysis(DashboardFile dashboardFile) {
		super(dashboardFile);
	}

	@Override
	public String getName() {
		return "Widget analysis";
	}

	@Override
	public String analyse() {
		
		StringBuffer buffer = new StringBuffer();
		
		if(dashboard != null) {
			DecimalFormat df = new DecimalFormat("#.#####");
			
			formatMetric(buffer, new NgoBalance(dashboard, GEType.ALL_TYPES, "Balance"), df);
			//formatMetric(buffer, new KimBalance(dashboard), df);
			formatMetric(buffer, new Equilibrium(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoSymmetry(dashboard, GEType.ALL_TYPES, "Symmetry"), df);
			//formatMetric(buffer, new KimSymmetry(dashboard), df);
			formatMetric(buffer, new Sequence(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Cohesion(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Unity(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Proportion(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Simplicity(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Density(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Regularity(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Economy(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new Homogenity(dashboard, GEType.ALL_TYPES), df);
		}
		
		return buffer.toString();
	}

}

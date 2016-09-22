package cz.vutbr.fit.dash.eval.analysis;

import java.text.DecimalFormat;

import cz.vutbr.fit.dash.eval.metric.Cohesion;
import cz.vutbr.fit.dash.eval.metric.Density;
import cz.vutbr.fit.dash.eval.metric.Economy;
import cz.vutbr.fit.dash.eval.metric.Equilibrium;
import cz.vutbr.fit.dash.eval.metric.Homogenity;
import cz.vutbr.fit.dash.eval.metric.KimBalance;
import cz.vutbr.fit.dash.eval.metric.KimSymmetry;
import cz.vutbr.fit.dash.eval.metric.NgoBalance;
import cz.vutbr.fit.dash.eval.metric.NgoSymmetry;
import cz.vutbr.fit.dash.eval.metric.Proportion;
import cz.vutbr.fit.dash.eval.metric.Regularity;
import cz.vutbr.fit.dash.eval.metric.Sequence;
import cz.vutbr.fit.dash.eval.metric.Simplicity;
import cz.vutbr.fit.dash.eval.metric.Unity;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public class WidgetAnalysis extends AbstractAnalysis implements IAnalysis {

	public WidgetAnalysis(Dashboard dashboard) {
		super(dashboard);
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

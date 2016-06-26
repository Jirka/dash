package cz.vutbr.fit.dash.analyses;

import java.text.DecimalFormat;

import cz.vutbr.fit.dash.metric.NgoBalance;
import cz.vutbr.fit.dash.metric.Cohesion;
import cz.vutbr.fit.dash.metric.Density;
import cz.vutbr.fit.dash.metric.Economy;
import cz.vutbr.fit.dash.metric.Equilibrium;
import cz.vutbr.fit.dash.metric.Homogenity;
import cz.vutbr.fit.dash.metric.KimBalance;
import cz.vutbr.fit.dash.metric.KimSymmetry;
import cz.vutbr.fit.dash.metric.Proportion;
import cz.vutbr.fit.dash.metric.Regularity;
import cz.vutbr.fit.dash.metric.Sequence;
import cz.vutbr.fit.dash.metric.Simplicity;
import cz.vutbr.fit.dash.metric.NgoSymmetry;
import cz.vutbr.fit.dash.metric.Unity;
import cz.vutbr.fit.dash.model.Dashboard;

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
			
			formatMetric(buffer, new NgoBalance(dashboard, "Balance"), df);
			//formatMetric(buffer, new KimBalance(dashboard), df);
			formatMetric(buffer, new Equilibrium(dashboard), df);
			formatMetric(buffer, new NgoSymmetry(dashboard, "Symmetry"), df);
			//formatMetric(buffer, new KimSymmetry(dashboard), df);
			formatMetric(buffer, new Sequence(dashboard), df);
			formatMetric(buffer, new Cohesion(dashboard), df);
			formatMetric(buffer, new Unity(dashboard), df);
			formatMetric(buffer, new Proportion(dashboard), df);
			formatMetric(buffer, new Simplicity(dashboard), df);
			formatMetric(buffer, new Density(dashboard), df);
			formatMetric(buffer, new Regularity(dashboard), df);
			formatMetric(buffer, new Economy(dashboard), df);
			formatMetric(buffer, new Homogenity(dashboard), df);
		}
		
		return buffer.toString();
	}

}

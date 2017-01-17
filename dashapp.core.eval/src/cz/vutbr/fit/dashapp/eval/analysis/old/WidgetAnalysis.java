package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoCohesion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEconomy;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoHomogenity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoProportion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRegularity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSimplicity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoUnity;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class WidgetAnalysis extends AbstractAnalysis implements IAnalysis {

	@Override
	public String getName() {
		return "Widget analysis";
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		Dashboard dashboard = dashboardFile.getDashboard(true);
		if(dashboard != null) {
			DecimalFormat df = new DecimalFormat("#.#####");
			
			formatMetric(buffer, new NgoBalance().measure(dashboard, GEType.ALL_TYPES), df);
			//formatMetric(buffer, new KimBalance().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoEquilibrium().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoSymmetry().measure(dashboard, GEType.ALL_TYPES), df);
			//formatMetric(buffer, new KimSymmetry().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoSequence().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoCohesion().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoUnity().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoProportion().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoSimplicity().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoDensity().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoRegularity().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoEconomy().measure(dashboard, GEType.ALL_TYPES), df);
			formatMetric(buffer, new NgoHomogenity().measure(dashboard, GEType.ALL_TYPES), df);
		}
		
		return buffer.toString();
	}

}

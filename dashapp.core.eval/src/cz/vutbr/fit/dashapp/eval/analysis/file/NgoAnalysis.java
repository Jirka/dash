package cz.vutbr.fit.dashapp.eval.analysis.file;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractFileAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.IFileAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoCohesion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEconomy;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoHomogenity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoProportion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRegularity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRhythm;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSimplicity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoUnity;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoAnalysis extends AbstractFileAnalysis implements IFileAnalysis {

	@Override
	public String getLabel() {
		return "Ngo analysis";
	}

	@Override
	public String processFile(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		Dashboard dashboard = dashboardFile.getDashboard(true);
		if(dashboard != null) {
			final DecimalFormat df = new DecimalFormat("#.#####");
			
			formatMetric(buffer, new NgoBalance().measure(dashboard), df);
			//formatMetric(buffer, new KimBalance().measure(dashboard), df);
			formatMetric(buffer, new NgoEquilibrium().measure(dashboard), df);
			formatMetric(buffer, new NgoSymmetry().measure(dashboard), df);
			//formatMetric(buffer, new KimSymmetry().measure(dashboard), df);
			formatMetric(buffer, new NgoSequence().measure(dashboard), df);
			formatMetric(buffer, new NgoCohesion().measure(dashboard), df);
			formatMetric(buffer, new NgoUnity().measure(dashboard), df);
			formatMetric(buffer, new NgoProportion().measure(dashboard), df);
			formatMetric(buffer, new NgoSimplicity().measure(dashboard), df);
			formatMetric(buffer, new NgoDensity().measure(dashboard), df);
			formatMetric(buffer, new NgoRegularity().measure(dashboard), df);
			formatMetric(buffer, new NgoEconomy().measure(dashboard), df);
			formatMetric(buffer, new NgoHomogenity().measure(dashboard), df);
			formatMetric(buffer, new NgoRhythm().measure(dashboard), df);
		}
		
		return buffer.toString();
	}

}

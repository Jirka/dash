package cz.vutbr.fit.dashapp.eval.analysis;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;

public abstract class AbstractAnalysis implements IAnalysis {
	
	protected Dashboard dashboard;
	protected DashboardFile dashboardFile;

	public AbstractAnalysis(DashboardFile dashboardFile) {
		this.dashboardFile = dashboardFile;
		// TODO temporary solution
		getDashboard();
	}
	
	public Dashboard getDashboard() {
		if(dashboard == null) {
			dashboardFile.getDashboard(true);
		}
		return dashboard;
	}

	protected void formatMetric(StringBuffer buffer, IMetric metric, DecimalFormat df) {
		Object m = metric.measure();
		if(m instanceof Double) {
			buffer.append(metric.getName() + " = " + df.format(m) + "\n");
		} else if(m instanceof Object[]) {
			int i = 0;
			Object[] mm = (Object[]) m;
			for(String name : metric.getSubNames()) {
				//if(i == 0) // TODO REMOVE
				if(mm[i] instanceof Double) {
					buffer.append(formatCompoundName(metric.getName(), name) + " = " + df.format(mm[i]) + "\n");
				} else {
					buffer.append(formatCompoundName(metric.getName(), name) + " = " + mm[i].toString() + "\n");
				}
				i++;
			}
		}
	}
	
	protected String formatCompoundName(String name, String subName) {
		if(subName != null && !subName.isEmpty()) {
			return name + " " + subName;
		}
		return name;
	}

}

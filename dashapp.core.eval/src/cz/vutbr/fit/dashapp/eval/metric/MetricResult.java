package cz.vutbr.fit.dashapp.eval.metric;

/**
 * Tool to store result of metric.
 * 
 * @author Jiri Hynek
 *
 */
public class MetricResult {
	
	public String name;
	public String initials;
	public Object value;
	public boolean disabled = false;
	
	public static enum MetricResultAttribute {
		NAME, INITIALS, VALUE
	}
	
	public MetricResult(String name, String initials, Object value) {
		this.name = name;
		this.initials = initials;
		this.value = value;
	}

	public boolean isSameKind(MetricResult actResult) {
		return name.equals(actResult.name) && initials.equals(actResult.initials) &&
				value.getClass() == actResult.value.getClass();
	}

	public double number() {
		if(value instanceof Integer) {
			return (int) value;
		} else if(value instanceof Double) {
			return (double) value;
		}
		return Double.NaN;
	}
	
	public String toString(MetricResultAttribute attribute) {
		switch (attribute) {
		case NAME:
			return name;
		case INITIALS:
			return initials;
		case VALUE:
			return value.toString();
		}
		return "";
	}
	
	public static void printLine(StringBuffer sb, MetricResult[] metricResults, MetricResultAttribute attribute) {
		int i = 0;
		while (i < metricResults.length) {
			sb.append(metricResults[i].toString(attribute));
			i++;
			if(i == metricResults.length) {
				sb.append("\n");
			} else {
				sb.append("\t");
			}
		}
	}
}

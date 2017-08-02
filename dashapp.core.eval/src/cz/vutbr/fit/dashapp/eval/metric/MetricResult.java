package cz.vutbr.fit.dashapp.eval.metric;

public class MetricResult {
	
	public String name;
	public String inicials;
	public Object value;
	public boolean disabled = false;
	
	public MetricResult(String name, String initials, Object value) {
		this.name = name;
		this.inicials = initials;
		this.value = value;
	}

	public boolean isSameKind(MetricResult actResult) {
		return name.equals(actResult.name) && inicials.equals(actResult.inicials) &&
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
}

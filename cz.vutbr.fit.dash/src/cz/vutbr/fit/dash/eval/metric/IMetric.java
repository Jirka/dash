package cz.vutbr.fit.dash.eval.metric;

public interface IMetric {
	
	public String getName();
	
	public String getInicials();
	
	public String[] getSubNames();
	
	public Object measure();

}

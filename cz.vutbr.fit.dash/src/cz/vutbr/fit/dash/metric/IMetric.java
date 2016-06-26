package cz.vutbr.fit.dash.metric;

public interface IMetric {
	
	public String getName();
	
	public String getInicials();
	
	public String[] getSubNames();
	
	public Object measure();

}

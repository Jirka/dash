package cz.vutbr.fit.dashapp.eval.metric;

/**
 * Abstract implementation of metric which measures one or more aspects of dashboard represented by dashboard file.
 * 
 * It provides string name of metric.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractMetric implements IMetric {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}

}

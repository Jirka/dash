package cz.vutbr.fit.dashapp.eval.metric;

public abstract class AbstractMetric implements IMetric {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

}

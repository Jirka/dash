package cz.vutbr.fit.dashapp.eval.analysis;

/**
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractAnalysis implements IAnalysis {
	
	public AbstractAnalysis() {
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

}

package cz.vutbr.fit.dashapp.util.quadrant;

import cz.vutbr.fit.dashapp.model.Constants.Quadrant;

/**
 * 
 * Extensible tool which helps to update map of quadrants.
 * 
 * @author Jiri Hynek
 *
 * @param <V>
 * @param <O>
 */
public abstract class QuadrantUpdater<V, O> {
	
	protected QuadrantMap<V> map;
	protected O[] list;
	protected Quadrant q;
	protected O o;
	protected V v;

	public QuadrantUpdater(QuadrantMap<V> map, O[] list, boolean createNewMap) {
		if(createNewMap) {
			this.map = new QuadrantMap<V>(map);
		} else {
			this.map = map;
		}
		this.list = list;
	}
	
	public QuadrantUpdater(QuadrantMap<V> map, boolean createNewMap) {
		this(map, null, createNewMap);
	}

	public QuadrantMap<V> perform() {
		prePerform();
		for (Quadrant q : Quadrant.values()) {
			this.q = q;
			this.v = map.get(q);
			if(list != null) {
				this.o = list[q.getIndex()];
			}
			map.replace(q, computeValue());
		}
		return map;
	}
	
	protected void prePerform() {
	}

	protected abstract V computeValue();

}

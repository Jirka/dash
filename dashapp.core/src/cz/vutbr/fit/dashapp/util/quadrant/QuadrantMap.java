package cz.vutbr.fit.dashapp.util.quadrant;

import java.util.HashMap;

import cz.vutbr.fit.dashapp.model.Cloneable;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;

/**
 * Generic hash map with a key representing quadrant of screen.
 * 
 * @author Jiri Hynek
 *
 * @param <V>
 */
public class QuadrantMap<V> extends HashMap<Quadrant, V> {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8794760346656778679L;

	public QuadrantMap(QuadrantMap<V> map) {
		copy(map);
	}
	
	private void copy(QuadrantMap<V> map) {
		for (java.util.Map.Entry<Quadrant, V> e : map.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	public QuadrantMap(V initValue) {
		init(initValue);
	}

	@SuppressWarnings("unchecked")
	public void init(V initValue) {
		for (Quadrant q : Quadrant.values()) {
			if(initValue instanceof Cloneable) {
				initValue = (V) ((Cloneable) initValue).copy();
			}
			put(q, initValue);
		}
	}
}

package cz.vutbr.fit.dashapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jiri Hynek
 *
 */
public abstract class WorkspaceFile implements IWorkspaceFile {
	
	/**
	 * model definition
	 */
	protected DashAppModel model;
	protected Map<Object, Object> cacheObjects;
	
	public WorkspaceFile(DashAppModel model) {
		this.model = model;
	}
	
	/**
	 * 
	 * @return model
	 */
	@Override
	public DashAppModel getModel() {
		return model;
	}

	/**
	 * 
	 * @param model
	 */
	public void setModel(DashAppModel model) {
		this.model = model;
	}
	
	@Override
	public Object getCachedObject(Object key) {
		if(cacheObjects != null) {
			return cacheObjects.get(key);
		}
		return null;
	}
	
	@Override
	public void putIntoCache(Object key, Object value) {
		if(cacheObjects == null) {
			cacheObjects = new HashMap<>();
		}
		cacheObjects.put(key, value);
	}
	
	@Override
	public void clearCache() {
		if(cacheObjects != null) {
			cacheObjects.clear();
			cacheObjects = null;
		}
	}

}

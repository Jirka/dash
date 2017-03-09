package cz.vutbr.fit.dashapp.model;

public interface IWorkspaceFile {

	String getFileName();

	DashAppModel getModel();

	Object getCachedObject(Object key);

	void putIntoCache(Object key, Object value);
	
	void clearCache();

}

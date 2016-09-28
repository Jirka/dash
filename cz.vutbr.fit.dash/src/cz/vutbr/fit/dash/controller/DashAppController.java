package cz.vutbr.fit.dash.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class which provides access to event manager and property change listeners.
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppController {
	
	private static DashAppController controller;
	
	public static DashAppController getInstance() {
		if(controller == null) {
			controller = new DashAppController();
		}
		return controller;
	}
	
	private List<IPropertyChangeListener> listeners;
	private boolean listenersDisabled;
	private EventManager eventManager;
	
	public DashAppController() {
	}
	
	public void init() {
		eventManager = new EventManager(this);
		listeners = new ArrayList<IPropertyChangeListener>();
	}
	
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removePropertyChangeListener(IPropertyChangeListener toolBar) {
		listeners.remove(toolBar);
	}
	
	public boolean getListenersDisabled() {
		return listenersDisabled;
	}

	public void setListenersDisabled(boolean listenersDisabled) {
		this.listenersDisabled = listenersDisabled;
	}

	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		if(!getListenersDisabled()) {
			for (IPropertyChangeListener propertyChangeListener : listeners) {
				propertyChangeListener.firePropertyChange(propertyChangeEvent);
			}
		}
	}
	
	public static EventManager getEventManager() {
		return getInstance().eventManager;
	}

}

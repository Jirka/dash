package cz.vutbr.fit.dash.controller;

import java.util.ArrayList;
import java.util.List;

public class DashAppController {
	
	private static DashAppController controller;
	
	public static DashAppController getInstance() {
		if(controller == null) {
			controller = new DashAppController();
		}
		return controller;
	}
	
	private List<PropertyChangeListener> listeners;
	private boolean listenersDisabled;
	private EventManager eventManager;
	
	public DashAppController() {
	}
	
	public void init() {
		eventManager = new EventManager(this);
		listeners = new ArrayList<PropertyChangeListener>();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removePropertyChangeListener(PropertyChangeListener toolBar) {
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
			for (PropertyChangeListener propertyChangeListener : listeners) {
				propertyChangeListener.firePropertyChange(propertyChangeEvent);
			}
		}
	}
	
	public static EventManager getEventManager() {
		return getInstance().eventManager;
	}

}

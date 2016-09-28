package cz.vutbr.fit.dash.controller;

import cz.vutbr.fit.dash.controller.EventManager.EventKind;
import cz.vutbr.fit.dash.model.Dashboard;

/**
 * Container which stores property change event information.
 * 
 * @author Jiri Hynek
 *
 */
public class PropertyChangeEvent {
	
	public PropertyChangeEvent(EventKind propertyKind, Dashboard selectedDashboard, Change oldValue, Change newValue) {
		this.propertyKind = propertyKind;
		this.selectedDashboard = selectedDashboard;
		this.modelChange = oldValue;
		this.xmlChange = newValue;
	}
	
	public final EventKind propertyKind;
	public final Dashboard selectedDashboard;
	public final Change modelChange;
	public final Change xmlChange;
	
	public static class Change {
		public final Object oldValue;
		public final Object newValue;
		
		public Change(Object oldValue, Object newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}

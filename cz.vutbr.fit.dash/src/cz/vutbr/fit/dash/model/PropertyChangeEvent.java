package cz.vutbr.fit.dash.model;

import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;

public class PropertyChangeEvent {
	
	public PropertyChangeEvent(PropertyKind propertyKind, Object oldValue, Object newValue) {
		this.propertyKind = propertyKind;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public PropertyKind propertyKind;
	public Object oldValue;
	public Object newValue;
}

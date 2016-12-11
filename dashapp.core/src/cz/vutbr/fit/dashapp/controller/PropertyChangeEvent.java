package cz.vutbr.fit.dashapp.controller;

import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;

/**
 * Container which stores property change event information.
 * 
 * @author Jiri Hynek
 *
 */
public class PropertyChangeEvent {
	
	public PropertyChangeEvent(EventKind propertyKind, IWorkspaceFile selectedFile, Change oldValue, Change newValue) {
		this.propertyKind = propertyKind;
		this.selectedFile = selectedFile;
		this.modelChange = oldValue;
		this.xmlChange = newValue;
	}
	
	public final EventKind propertyKind;
	public final IWorkspaceFile selectedFile;
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

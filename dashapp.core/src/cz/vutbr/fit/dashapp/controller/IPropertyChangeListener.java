package cz.vutbr.fit.dashapp.controller;

/**
 * Main property change listener.
 * 
 * @author Jiri Hynek
 *
 */
public interface IPropertyChangeListener {

	public void firePropertyChange(PropertyChangeEvent e);
}

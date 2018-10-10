package cz.vutbr.fit.dashapp.view.dialog;

import javax.swing.*;

import java.awt.Component;
import java.beans.*;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppProgressDialog implements PropertyChangeListener {
	
	public static abstract class DashAppTask extends SwingWorker<Void, Void> {
		
		/**
		 * 
		 * @return main label
		 */
		public abstract Object getMainLabel();

		/**
		 * 
		 * @return message
		 */
		public abstract Object getMessage();
	}

    private ProgressMonitor monitor;
    private DashAppTask task;
	private Component parent;

    public DashAppProgressDialog(Component parent, DashAppTask task) {
    	this.parent = parent;
    	this.task = task;
    }

    /**
     * Invoked when the user presses the start button.
     */
    public void execute() {
		this.monitor = new ProgressMonitor(parent, task.getMainLabel(), "", 0, 100);
		task.addPropertyChangeListener(this);
		this.monitor.setProgress(0);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent e) {
    	System.out.println(e);
        if (e.getPropertyName() ==  "progress") {
            int progress = (Integer) e.getNewValue();
            monitor.setProgress(progress);
            String message = String.format("%d%%: %s.\n", progress, task.getMessage());
            monitor.setNote(message);
            if (monitor.isCanceled() || task.isDone()) {
            	task.cancel(true);
            }
        }
    }
}

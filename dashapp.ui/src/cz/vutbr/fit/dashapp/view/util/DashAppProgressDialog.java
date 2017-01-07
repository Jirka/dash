/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package cz.vutbr.fit.dashapp.view.util;

import javax.swing.*;

import java.awt.Component;
import java.beans.*;

public class DashAppProgressDialog implements PropertyChangeListener {
	
	public static abstract class DashAppTask extends SwingWorker<Void, Void> {
		
		public abstract Object getMainLabel();

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
        this.monitor.setProgress(0);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            monitor.setProgress(progress);
            String message = String.format("%d%%: %s.\n", progress, task.getMessage());
            monitor.setNote(message);
            if (monitor.isCanceled() || task.isDone()) {
            	task.cancel(true);
            }
        }

    }
}

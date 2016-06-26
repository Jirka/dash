package cz.vutbr.fit.dash.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cz.vutbr.fit.dash.model.DashAppModel;

public class OpenFolderAction extends AbstractAction {

	/**
	 * UID 
	 */
	private static final long serialVersionUID = -565363971398691873L;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// file picker //
	    	JFileChooser fc = new JFileChooser();
	        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
	        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        fc.setAcceptAllFileFilterUsed(false);
	        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        	File file = fc.getSelectedFile();
	        	if(file != null) {
	        		String path = file.getAbsolutePath();
	        		if(path != null) {
	        			DashAppModel.getInstance().setFolderPath(path);
	        		}
	        	}
	        }
		}
	}
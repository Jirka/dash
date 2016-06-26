package vin.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import vin.dialogs.VinDialogs;

/**
 * Class which contains actions.
 * 
 * @author jurij
 *
 */
public class VinActions {
	
	protected ActionSaveProfile actionSaveProfile;
	protected ActionNewProfile actionNewProfile;
	protected ActionOpenProfile actionOpenProfile;
	protected ActionExitProgram actionExitProgram;
	protected ActionPlay actionPlay;
	protected ActionStop actionStop;
	protected ActionDraw actionDraw;
	protected ActionColor actionColor;
	protected ActionPallete actionPallete;
	protected ActionZoomIn actionZoomIn;
	protected ActionZoomOut actionZoomOut;
	protected ActionDelete actionDelete;
	protected ActionGrayScale actionGrayScale;
	protected ActionAdaptive actionAdaptive;
	protected String ext;
	protected Color color;
	protected boolean timerEnabled;
	protected boolean drawEnabled;
	
	/**
	 * Constructor which creates actions.
	 */
	public VinActions() {
		actionSaveProfile = new ActionSaveProfile();
		actionNewProfile = new ActionNewProfile();
		actionOpenProfile = new ActionOpenProfile();
		actionExitProgram = new ActionExitProgram();
		actionDraw = new ActionDraw();
		actionPlay = new ActionPlay();
		actionStop = new ActionStop();
		actionColor = new ActionColor();
		actionPallete = new ActionPallete();
		actionZoomIn = new ActionZoomIn();
		actionZoomOut = new ActionZoomOut();
		actionDelete = new ActionDelete();
		actionGrayScale = new ActionGrayScale();
		actionAdaptive = new ActionAdaptive();
		drawEnabled = false;
		color = new Color(0);
	}
	
	/**
	 * Opens file dialog and ask user for file.
	 * 
	 * @param text
	 * @return selFile
	 */
	private File getImageFile(String text) {
		
		JFrame frame = new JFrame();
        frame.setBounds(200, 200, 500, 350);
		
		// file picker //
    	JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));
        fc.addChoosableFileFilter(new VinFilter("bmp"));
        fc.addChoosableFileFilter(new VinFilter("jpg"));
        fc.addChoosableFileFilter(new VinFilter("jpeg"));
        fc.addChoosableFileFilter(new VinFilter("gif"));
        fc.addChoosableFileFilter(new VinFilter("png"));
        frame.add(fc);
        fc.showDialog(frame, text);
        ext = fc.getFileFilter().getDescription();
        File file = fc.getSelectedFile();
        if(!(ext.equals("bmp") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))) {
        	if(file != null) {
        		System.out.println(ext);
        		int i = file.getAbsolutePath().lastIndexOf(".");
        		if(i > 0 && i < (file.getAbsolutePath().length()-1)) {
        			ext = file.getAbsolutePath().substring(i+1);
        		}
        		if(!(ext.equals("bmp") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))) {
        			ext = "bmp";
        		}
        	}
        }
        return (fc.getSelectedFile());
	}
	
	/**
	 * Checks if timer is on.
	 */
	private void checkTimer() {
		timerEnabled = ViewGui.getVinScrollPane().getVinSurface().isTimeEnabled();
    	if(timerEnabled) {
    		ViewGui.getVinScrollPane().getVinSurface().setTimeEnabled(false);
    	}
	}
	
	/**
	 * Sets timer on.
	 */
	private void setTimer() {
		if(timerEnabled) {
			ViewGui.getVinScrollPane().getVinSurface().setTimeEnabled(true);
		}
	}
	
	/**
	 * Returns boolean if drawing is enabled.
	 * 
	 * @return drawEnabled
	 */
	public boolean isDrawEnabled() {
    	return drawEnabled;
    }

	/**
	 * Returns color.
	 * 
	 * @return color
	 */
	public Color getColor() {
    	return color;
    }

	/**
	 * Returns reference to action which save profile.
	 * 
	 * @return actionSaveProfile
	 */
	public ActionSaveProfile getActionSaveProfile() {
		return actionSaveProfile;
	}
	
	/**
	 * Returns reference to action which opens new profile.
	 * 
	 * @return actionNewProfile
	 */
	public ActionNewProfile getActionNewProfile() {
		return actionNewProfile;
	}

	/**
	 * Returns reference to action which open profile.
	 * 
	 * @return actionOPenProfile
	 */
	public ActionOpenProfile getActionOpenProfile() {
		return actionOpenProfile;
	}

	/**
	 * Returns reference to action which exit program.
	 * 
	 * @return actionExitProgram
	 */
	public ActionExitProgram getActionExitProgram() {
		return actionExitProgram;
	}
	
	/**
	 * Returns reference to action which stop drawing.
	 * 
	 * @return actionStop
	 */
	public ActionStop getActionStop() {
		return actionStop;
	}

	/**
	 * Returns reference to action which star drawing.
	 * 
	 * @return actionPLay
	 */
	public ActionPlay getActionPlay() {
		return actionPlay;
	}
	
	/**
	 * Returns reference to action which draw points.
	 * 
	 * @return actionDraw
	 */
	public ActionDraw getActionDraw() {
		return actionDraw;
	}
	
	
	/**
	 * Returns reference to action which change color.
	 * 
	 * @return actionColor
	 */
	public ActionColor getActionColor() {
    	return actionColor;
    }
	
	/**
	 * 
	 * @return actionPallete
	 */
	public ActionPallete getActionPallete() {
    	return actionPallete;
    }

	/**
	 * Returns reference to action which zoom in picture.
	 * 
	 * @return actionZoomIn
	 */
	public ActionZoomIn getActionZoomIn() {
    	return actionZoomIn;
    }

	/**
	 * Returns reference to action which zoom out picture.
	 * 
	 * @return actionZoomOut
	 */
	public ActionZoomOut getActionZoomOut() {
    	return actionZoomOut;
    }
	
	/**
	 * Returns reference to action which delete points.
	 * 
	 * @return actionDelete
	 */
	public ActionDelete getActionDelete() {
    	return actionDelete;
    }

	/**
	 * Returns reference to action which change color of picture to gray scale.
	 * 
	 * @return actionGrayScale
	 */
	public ActionGrayScale getActionGrayScale() {
    	return actionGrayScale;
    }

	/**
	 * Returns reference to action which make adaptive treshold.
	 * 
	 * @return actionAdaptive
	 */
	public ActionAdaptive getActionAdaptive() {
    	return actionAdaptive;
    }


	/**
	 * Action which closes application.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionExitProgram extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
    /**
	 * Action which creates new profile.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionNewProfile extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	ViewGui.getVinDialogNewFile().setVisible(true);
        }
    }
    
    /**
	 * Action which closes application.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionSaveProfile extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	checkTimer();
        	
        	File selFile = getImageFile("Save");
        	
        	if(selFile == null) {
        		setTimer();
        		return;
        	}
        	
        	if(selFile.exists()) {
				int result = VinDialogs.YesNoCancel("Do you realy want to rewrite file" + selFile + ".");
				if(result != 0) {
					setTimer();
					return;
				}
			}
        	
        	ViewGui.getVinScrollPane().getVinSurface().save(selFile, ext);
        }
    }
    
    /**
	 * Action which open existing profile.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionOpenProfile extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	checkTimer();
        	
        	timerEnabled = ViewGui.getVinScrollPane().getVinSurface().isTimeEnabled();
        	if(timerEnabled) {
        		ViewGui.getVinScrollPane().getVinSurface().setTimeEnabled(false);
        	}
        	
        	File selFile = getImageFile("Open");
        	
        	if(selFile == null) {
        		setTimer();
        		return;
        	}
        	
        	ViewGui.getVinScrollPane().getVinSurface().open(selFile);
        }
    }

    /**
     * Action which starts drawing.
     * 
     * @author jurij
     *
     */
    @SuppressWarnings("serial")
	class ActionPlay extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
            ViewGui.getVinScrollPane().getVinSurface().setTimeEnabled(true);
        }
    }
    
    /**
     * Action which stops drawing.
     * 
     * @author jurij
     *
     */
    @SuppressWarnings("serial")
	class ActionStop extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
            ViewGui.getVinScrollPane().getVinSurface().setTimeEnabled(false);
        }
    }
    
    /**
     * Class for file filter.
     * 
     * @author jurij
     *
     */
    class VinFilter extends FileFilter {
    	
    	String ext;
    	
    	public VinFilter(String ext) {
	        this.ext = ext;
        }

		@Override
        public boolean accept(File file) {
	        return file.isDirectory() || file.getName().toLowerCase().equals(ext);
        }

		@Override
        public String getDescription() {
	        return ext;
        }
    	
    }
    
    /**
	 * Action which draw points.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionDraw extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	drawEnabled = (!drawEnabled);
        	ViewGui.getVinScrollPane().getVinSurface().repaint();
        	
        	if(drawEnabled) {
        		System.out.println("cursor");
        		ViewGui.getVinScrollPane().getVinSurface().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        	} else {
        		ViewGui.getVinScrollPane().getVinSurface().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        	}
        }
    }
    
    /**
	 * Action which changes color.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionColor extends AbstractAction
    {
        @SuppressWarnings("static-access")
        public void actionPerformed(ActionEvent e) {
        	JFrame frame = new JFrame();
            frame.setBounds(200, 200, 500, 350);
        	JColorChooser chooser = new JColorChooser();
        	Color color1 = chooser.showDialog(frame, "Choose color", color);
        	if(color1 != null) {
        		color = color1;
        		ViewGui.getVinToolbar().setColor(color);
        	}
        }
    }
    
    /**
	 * Action which changes pallete.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionPallete extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        }
    }
    
    /**
	 * Action which zoom in.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionZoomIn extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	ViewGui.getVinScrollPane().getVinSurface().zoom(1);
        }
    }
    
    /**
	 * Action which zoom out.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionZoomOut extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	ViewGui.getVinScrollPane().getVinSurface().zoom(-1);
        }
    }
    
    /**
	 * Action which deletes points.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionDelete extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	ViewGui.getVinScrollPane().getVinSurface().deletePoints();
        }
    }
    
    /**
	 * Action which changes color of picture to grayscale.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionGrayScale extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	ViewGui.getVinScrollPane().getVinSurface().changeToGrayScale();
        }
    }
    
    /**
	 * Action which makes adaptive treshold.
	 * 
	 * @author jurij
	 *
	 */
    @SuppressWarnings("serial")
	class ActionAdaptive extends AbstractAction
    {
        public void actionPerformed(ActionEvent e) {
        	
        	JButton btn = (JButton) e.getSource();
        	
        	int i = btn.getMnemonic();
        	
        	if(i == 1 && !ViewGui.getVinScrollPane().getVinSurface().grayScale) {
        		i = 0;
        	}
        	
        	ViewGui.getVinScrollPane().getVinSurface().prah_integral(i);
        }
    }

}

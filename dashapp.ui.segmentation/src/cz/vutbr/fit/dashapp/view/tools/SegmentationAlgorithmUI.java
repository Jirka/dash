package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationDebugListener;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.action.IDashActionUI;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.dialog.GridLayoutFormDialog;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog.DashAppTask;
import extern.ImagePreview;
import tmp.Histogram;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationAlgorithmUI implements IDashActionUI {
	
	protected ISegmentationAlgorithm segmentationAlgorithm;

	public static final boolean DEFAULT_DEBUG_OPTION = false;
	private boolean debug;
	private JCheckBox debugCheckBox;
	
	public SegmentationAlgorithmUI(ISegmentationAlgorithm algorithm) {
		this.segmentationAlgorithm = algorithm;
		this.debug = DEFAULT_DEBUG_OPTION;
	}
	
	public String getLabel() {
		return segmentationAlgorithm.getName();
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public void perform(ActionEvent e) {
		if(getSettings()) {
			SegmentationAlgorithmTask task = new SegmentationAlgorithmTask(segmentationAlgorithm, debug);
			DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
			monitor.execute();
		}
	}
	
	private boolean getSettings() {
		SegmentationAlgorithmSettingsDialog settingsDialog = new SegmentationAlgorithmSettingsDialog(this);
		return settingsDialog.showConfirmDialog();
	}
	
	protected void getCustomSettings(JPanel panel) {
		// folder regex
		debugCheckBox = new JCheckBox("Enable debug mode", debug);
		panel.add(debugCheckBox);
        panel.add(new JLabel());
		
        // extend this class if required
	}
	
	protected void processCustomSettings() {
		debug = debugCheckBox.isSelected();
		
		// extend this class if required
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class SegmentationAlgorithmSettingsDialog extends GridLayoutFormDialog {
		
		private SegmentationAlgorithmUI algorithmUI;

		public SegmentationAlgorithmSettingsDialog(SegmentationAlgorithmUI analysisUI) {
			super(analysisUI.segmentationAlgorithm.getName() + " Settings");
			this.algorithmUI = analysisUI;
		}
		
		@Override
		protected void addCustomWidgets(JPanel panel) {
			algorithmUI.getCustomSettings(panel);
		}
		
		@Override
		protected boolean processCustomWidgets() {
			algorithmUI.processCustomSettings();
			
			return true;
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class SegmentationAlgorithmTask extends DashAppTask implements ISegmentationDebugListener {
		
		private ISegmentationAlgorithm segmentationAlgorithm;
		private boolean debug;
		private String message;

		public SegmentationAlgorithmTask(ISegmentationAlgorithm segmentationAlgorithm, boolean debug) {
			super();
			this.segmentationAlgorithm = segmentationAlgorithm;
			this.debug = debug;
		}
		
        @Override
        public Void doInBackground() {
        	try {
        		Dashboard selectedDashboard = DashAppUtils.getSelectedDashboard();
				if(selectedDashboard != null) {
					// get image from file (not from canvas)
					BufferedImage image = selectedDashboard.getDashboardFile().getImage();;
					// get actual image from canvas (working copy)
					// BufferedImage image = DashAppView.getInstance().getDashboardView().getCanvas().getImage();
					if(image != null) {
						setProgress(1);
						message = "processing image..." + selectedDashboard.getDashboardFile().getFileName();
						// TODO: use message inside segmentation algorithm
						if(debug) {
							segmentationAlgorithm.addSegmentationDebugListener(this);
						}
						Dashboard newDashboard = segmentationAlgorithm.processImage(image);
						if(newDashboard != null) {
							// update dashboard
							DashAppController.getEventManager().updateDashboard(newDashboard);
						}
						setProgress(99);
						message = "reloading image...";
						// repaint canvas
						DashAppView.getInstance().getDashboardView().getCanvas().repaint();
					}
				}
    			setProgress(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
            return null;
        }

        @Override
        public void done() {
        	System.out.println("segmentation done");
        }

		@Override
		public Object getMainLabel() {
			return "Segmenting dashoard";
		}

		@Override
		public Object getMessage() {
			return message;
		}
		
		@Override
		public void debugImage(String label, BufferedImage image, ISegmentationAlgorithm segmentationAlgorithm) {
			if(debug) {
				new ImagePreview(image, label).openWindow(800,600,0.8);
			}
		}

		@Override
		public void debugHistogram(String label, int[] histogram, ISegmentationAlgorithm segmentationAlgorithm) {
			if(debug) {
				new Histogram(label, histogram).openWindow();
			}
		}
    }

}

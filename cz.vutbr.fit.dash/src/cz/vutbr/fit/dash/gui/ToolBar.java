package cz.vutbr.fit.dash.gui;

import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import cz.vutbr.fit.dash.actions.WidgetAction;
import cz.vutbr.fit.dash.actions.XMLAction;
import cz.vutbr.fit.dash.actions.AnalysisAction;
import cz.vutbr.fit.dash.actions.AttachAction;
import cz.vutbr.fit.dash.actions.FullScreenAction;
import cz.vutbr.fit.dash.actions.HistoryAction;
import cz.vutbr.fit.dash.actions.NewFileAction;
import cz.vutbr.fit.dash.actions.OpenFolderAction;
import cz.vutbr.fit.dash.actions.ZoomAction;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;
import cz.vutbr.fit.dash.model.DashAppModel.WidgetActionKind;
import cz.vutbr.fit.dash.model.PropertyChangeEvent;
import cz.vutbr.fit.dash.model.PropertyChangeListener;

/**
 * Class which contains toolbar.
 * 
 * @author Jiri Hynek
 *
 */
public class ToolBar implements IComponent, PropertyChangeListener {

	protected JToolBar bar;
	protected JButton btnZoomIn, btnZoomOut;
	protected static FullScreenAction fullScreenAction = new FullScreenAction();
	
	/**
	 * Creates toolbar.
	 */
	public ToolBar() {
		initGUI();
		initListeners();
	}
	
	private void initGUI() {
		bar = new JToolBar(JToolBar.HORIZONTAL);
		
		// PROFILE //
        Utils.addButton(bar, "Open files", "/icons/Open file.png", new OpenFolderAction(), 0);
        Utils.addButton(bar, "Save all", "/icons/Save as.png", null, 0);
        
        bar.addSeparator();
        
        Utils.addButton(bar, "New", "/icons/Document.png", new NewFileAction(), 0);
        
        // XML //
        Utils.addButton(bar, "Refresh", "/icons/Refresh.png", new XMLAction(XMLAction.REFRESH), 0);
        Utils.addButton(bar, "Save", "/icons/Save.png", new XMLAction(XMLAction.SAVE), 0);
        
        bar.addSeparator();
        
        // HISTORY
        
        Utils.addButton(bar, "Undo", "/icons/Undo.png", new HistoryAction(HistoryAction.UNDO), 0).setPreferredSize(new Dimension(24, 24));
		Utils.addButton(bar, "Redo", "/icons/Redo.png", new HistoryAction(HistoryAction.REDO), 0).setPreferredSize(new Dimension(24, 24));
		
		bar.addSeparator();
        
        // ZOOM //
        btnZoomIn = Utils.addButton(bar, "Zoom in", "/icons/Zoom in.png", new ZoomAction(ZoomAction.ZOOM_IN), 0);
        btnZoomOut = Utils.addButton(bar, "Zoom out", "/icons/Zoom out.png", new ZoomAction(ZoomAction.ZOOM_OUT), 0);
        Utils.addButton(bar, "Full screen", "/icons/Zoom.png", fullScreenAction, 0);
        
        bar.addSeparator();
        
        ButtonGroup group = new ButtonGroup();
        JToggleButton btn = Utils.addToggleButton(bar, "View Image", "/icons/Monitor.png", new WidgetAction(WidgetActionKind.VIEW), 0);
        btn.getModel().setGroup(group);
        Utils.addToggleButton(bar, "Define dashboard area", "/icons/Billboard.png", new WidgetAction(WidgetActionKind.BOUND), 0).getModel().setGroup(group);
        Utils.addToggleButton(bar, "Select graphical element", "/icons/Pointer.png", new WidgetAction(WidgetActionKind.SELECT), 0).getModel().setGroup(group);
        Utils.addToggleButton(bar, "Insert grapical element", "/icons/Edit.png", new WidgetAction(WidgetActionKind.INSERT), 0).getModel().setGroup(group);
        // TODO
        //Utils.addToggleButton(bar, "Magic Wand", "/icons/Edit.png", new WidgetAction(WidgetActionKind.WAND), 0).getModel().setGroup(group);
        group.setSelected(btn.getModel(), true);
        
        bar.addSeparator();
        
        Utils.addToggleButton(bar, "View Image", "/icons/Open lock.png",
        		new AttachAction(new ImageIcon(Utils.class.getResource("/icons/Lock.png")),
        						new ImageIcon(Utils.class.getResource("/icons/Open lock.png"))), 0);
        
        bar.addSeparator();
        
        Utils.addButton(bar, "Actual Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.ACTUAL_ANALYSIS), 0);
        Utils.addButton(bar, "Colorfulness Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.COLORFULNESS_ANALYSIS), 0);
        Utils.addButton(bar, "Color Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.COLOR_ANALYSIS), 0);
        Utils.addButton(bar, "Grayscale Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.GRAYSCALE_ANALYSIS), 0);
        Utils.addButton(bar, "Threshold Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.THRESHOLD_ANALYSIS), 0);
        Utils.addButton(bar, "Widget Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.WIDGET_ANALYSIS), 0);
        
        
	}

	private void initListeners() {
		DashAppModel.getInstance().addPropertyChangeListener(this);
	}
    
	@Override
    public JComponent getComponent(){
        return bar;
    }

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == PropertyKind.ZOOM_LEVEL) {
			int oldValue = (Integer) e.oldValue;
			int newValue = (Integer) e.newValue; 
			if(newValue - oldValue > 0) {
				if(newValue == 1) {
					btnZoomOut.setEnabled(true);
				} else if(newValue == DashAppModel.zoomField.length-1) {
					btnZoomIn.setEnabled(false);
				}
			} else {
				if(newValue == DashAppModel.zoomField.length-2) {
					btnZoomIn.setEnabled(true);
				} else if(newValue == 0) {
					btnZoomOut.setEnabled(false);
				}
			}
		}
	}
	
	public void dispose() {
		DashAppModel.getInstance().removePropertyChangeListener(this);
	}

}

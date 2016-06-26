package cz.vutbr.fit.dash.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.vutbr.fit.dash.analyses.WidgetAnalysis;
import cz.vutbr.fit.dash.analyses.ActualAnalysis;
import cz.vutbr.fit.dash.analyses.ColorAnalysis;
import cz.vutbr.fit.dash.analyses.ColorfulnessAnalysis;
import cz.vutbr.fit.dash.analyses.GrayscaleAnalysis;
import cz.vutbr.fit.dash.analyses.IAnalysis;
import cz.vutbr.fit.dash.analyses.NgoLayoutAnalysis;
import cz.vutbr.fit.dash.analyses.RasterAnalysis;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;

public class AnalysisAction extends AbstractAction {
	
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 8636377644255186208L;
	
	//public static final int NGO_ANALYSES = 0;

	public static final int ACTUAL_ANALYSIS = 0;
	public static final int THRESHOLD_ANALYSIS = 1;
	public static final int COLOR_ANALYSIS = 2;
	public static final int GRAYSCALE_ANALYSIS = 3;
	public static final int WIDGET_ANALYSIS = 4;
	public static final int COLORFULNESS_ANALYSIS = 5;
	
	private int kind;

	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;
	
	public AnalysisAction(int kind) {
		this.kind = kind;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
		IAnalysis analysis = null;
		String report = "";
		if(kind == ACTUAL_ANALYSIS) {
			analysis = new ActualAnalysis(selectedDashboard);
		} else if(kind == THRESHOLD_ANALYSIS) {
			analysis = new RasterAnalysis(selectedDashboard);
		} else if(kind == COLOR_ANALYSIS) {
			analysis = new ColorAnalysis(selectedDashboard);
		} else if(kind == GRAYSCALE_ANALYSIS) {
			analysis = new GrayscaleAnalysis(selectedDashboard);
		} else if(kind == WIDGET_ANALYSIS) {
			analysis = new WidgetAnalysis(selectedDashboard);
		} else if(kind == COLORFULNESS_ANALYSIS) {
			analysis = new ColorfulnessAnalysis(selectedDashboard);
		}
		
		if(analysis != null) {
			report = analysis.analyse();
			JFrame frame = new JFrame(analysis.getName());
			
			Toolkit toolkit = frame.getToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			frame.setBounds((screenSize.width/2-WIDTH/2), screenSize.height/2-HEIGHT/2, WIDTH, HEIGHT);
			
			JTextArea area = new JTextArea(report);
			area.setEditable(false);
			frame.add(new JScrollPane(area), BorderLayout.CENTER);
			frame.setVisible(true);
		}
	}
}
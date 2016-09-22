package cz.vutbr.fit.dash.view.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.vutbr.fit.dash.eval.analysis.ActualAnalysis;
import cz.vutbr.fit.dash.eval.analysis.ColorAnalysis;
import cz.vutbr.fit.dash.eval.analysis.ColorfulnessAnalysis;
import cz.vutbr.fit.dash.eval.analysis.GrayscaleAnalysis;
import cz.vutbr.fit.dash.eval.analysis.IAnalysis;
import cz.vutbr.fit.dash.eval.analysis.RasterAnalysis;
import cz.vutbr.fit.dash.eval.analysis.WidgetAnalysis;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.view.ToolBar;

public class AnalysisTool extends AbstractGUITool implements IGUITool {

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Actual Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.ACTUAL_ANALYSIS), 0);
		toolbar.addButton("Colorfulness Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.COLORFULNESS_ANALYSIS), 0);
		toolbar.addButton("Color Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.COLOR_ANALYSIS), 0);
		toolbar.addButton("Grayscale Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.GRAYSCALE_ANALYSIS), 0);
		toolbar.addButton("Threshold Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.THRESHOLD_ANALYSIS), 0);
		toolbar.addButton("Widget Analysis", "/icons/Statistics.png", new AnalysisAction(AnalysisAction.WIDGET_ANALYSIS), 0);
	}
	
	/**
	 * TODO
	 * 
	 * @author Jiri Hynek
	 *
	 */
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
}

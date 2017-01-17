package cz.vutbr.fit.dashapp.view.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.vutbr.fit.dashapp.eval.analysis.old.ActualAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.ColorAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.ColorfulnessAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.GrayscaleAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.IAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.RasterAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.old.WidgetAnalysis;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.ToolBar;

public class OldAnalysisTool extends AbstractGUITool implements IGUITool {

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
			
			DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
			IAnalysis analysis = null;
			String report = "";
			if(kind == ACTUAL_ANALYSIS) {
				analysis = new ActualAnalysis();
			} else if(kind == THRESHOLD_ANALYSIS) {
				analysis = new RasterAnalysis();
			} else if(kind == COLOR_ANALYSIS) {
				analysis = new ColorAnalysis();
			} else if(kind == GRAYSCALE_ANALYSIS) {
				analysis = new GrayscaleAnalysis();
			} else if(kind == WIDGET_ANALYSIS) {
				analysis = new WidgetAnalysis();
			} else if(kind == COLORFULNESS_ANALYSIS) {
				analysis = new ColorfulnessAnalysis();
			}
			
			if(analysis != null) {
				report = analysis.analyze(selectedDashboardFile);
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

package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

public class AnalysisTool extends AbstractGUITool implements IGUITool {
	
	AnalysisAction analysisAction;
	
	public AnalysisTool(List<IMetric> metrics) {
		analysisAction = new AnalysisAction(metrics);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Eval");
		menuBar.addItem(subMenu, "Analyze", analysisAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Perform analysis", "/icons/Statistics.png", analysisAction, 0);
	}
	
	/**
	 * Open folder action which handles open folder event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class AnalysisAction extends AbstractAction {
		
		/**
		 * UID 
		 */
		private static final long serialVersionUID = 7983656071195387498L;
		
		private List<IMetric> metrics;

		public AnalysisAction(List<IMetric> metrics) {
			this.metrics = metrics;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AnalysisPreferenceWindow window = new AnalysisPreferenceWindow();
			window.open();
		}		
	}

}

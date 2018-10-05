package cz.vutbr.fit.dashapp.view.tools.image;

import cz.vutbr.fit.dashapp.view.tools.image.action.BasicImageToolActions;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ImageActionFactory {
	
	public static AbstractImageToolAction[] getRecommendedActions() {
		return new AbstractImageToolAction[] {
				new BasicImageToolActions.ImageToolAction_Reset(),
				new BasicImageToolActions.ImageToolAction_Gray(),
				new BasicImageToolActions.ImageToolAction_Posterize(),
				new BasicImageToolActions.ImageToolAction_Edges(),
				new BasicImageToolActions.ImageToolAction_Inverse(),
				new BasicImageToolActions.ImageToolAction_EdgesInverse(),
				new BasicImageToolActions.ImageToolAction_Lines(),
				new BasicImageToolActions.ImageToolAction_MedianFilter(),
				new BasicImageToolActions.ImageToolAction_Sharpen(),
				new BasicImageToolActions.ImageToolAction_Threshold(),
				new BasicImageToolActions.ImageToolAction_Rectangles(),
				new BasicImageToolActions.ImageToolAction_Histogram()
		};
	}

}

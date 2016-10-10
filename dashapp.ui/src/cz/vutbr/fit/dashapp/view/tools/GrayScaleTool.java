package cz.vutbr.fit.dashapp.view.tools;

import cz.vutbr.fit.dashapp.view.Canvas;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class GrayScaleTool extends AbstractGUITool implements IGUITool {
	
	public void init(Canvas canvas) {
		super.init(canvas);
		// TODO don't delegate this job to canvas (remove corresponding code to this tool)
		canvas.setGrayScaleToolEnabled(true);
	}

}

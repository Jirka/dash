package cz.vutbr.fit.tools.segmentation;

import cz.vutbr.fit.view.tools.image.AbstractImageToolAction;

public class SegmentationImageActionFactory {
	
	public static AbstractImageToolAction[] getRecommendedActions() {
		return new AbstractImageToolAction[] {
				new SegmentationImageToolActions.ImageToolAction_EmphasizeSameColor(),
				new SegmentationImageToolActions.ImageToolAction_RemoveGradients(),
				new SegmentationImageToolActions.ImageToolAction_HoughLines(),
				new SegmentationImageToolActions.ImageToolAction_HoughLinesImage(),
				new SegmentationImageToolActions.ImageToolAction_HistogramThreshold11(),
				new SegmentationImageToolActions.ImageToolAction_HistogramThreshold12(),
				new SegmentationImageToolActions.ImageToolAction_HistogramThreshold21(),
				new SegmentationImageToolActions.ImageToolAction_HistogramThreshold22(),
				new SegmentationImageToolActions.ImageToolAction_SameColorRegions(),
				new SegmentationImageToolActions.ImageToolAction_SameColorDominantRegions(),
		};
	}

}

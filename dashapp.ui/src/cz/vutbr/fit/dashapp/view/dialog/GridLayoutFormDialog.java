package cz.vutbr.fit.dashapp.view.dialog;

import java.awt.GridLayout;

/**
 * Custom settings dialog
 * 
 * @author Jiri Hynek
 *
 */
public class GridLayoutFormDialog extends DashAppFormDialog {

	public GridLayoutFormDialog() {
		super(new GridLayout(0, 2, 5, 5));
	}
	
	public GridLayoutFormDialog(String label) {
		super(new GridLayout(0, 2, 5, 5), label);
	}

}

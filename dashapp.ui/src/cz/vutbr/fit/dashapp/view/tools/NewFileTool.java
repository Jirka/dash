package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.dialog.SimpleDialogs;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NewFileTool extends AbstractGUITool implements IGUITool {
	
	private static final String LABEL = "New";
	
	NewFileAction newFileAction;
	
	public NewFileTool() {
		this(false);
	}
	
	public NewFileTool(boolean addSeparator) {
		super(addSeparator);
		newFileAction = new NewFileAction();
	}
	
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		menuBar.addItem(subMenu, LABEL, newFileAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, "/icons/Document.png", newFileAction, 0);
	}
	
	/**
	 * New file action which handles new file creation event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class NewFileAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -7806859795031384430L;

		public NewFileAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// construct new file dialog GUI
			JTextField nameInput = new JTextField("new_file", 20);
			JSpinner spinnerWidth = new JSpinner();
			spinnerWidth.setValue(640);
			((JSpinner.DefaultEditor) spinnerWidth.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
			spinnerWidth.setPreferredSize(new Dimension(60, 20));
			JSpinner spinnerHeight = new JSpinner();
			((JSpinner.DefaultEditor) spinnerHeight.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
			spinnerHeight.setValue(480);
			spinnerHeight.setPreferredSize(new Dimension(60, 20));
			final JComponent[] inputs = new JComponent[] { new JLabel("Name"), nameInput, new JLabel("Width"),
					spinnerWidth, new JLabel("Height"), spinnerHeight };
			// ask user
			if (JOptionPane.showConfirmDialog(null, inputs, "Create empty dashboard", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
				int width = new Integer(spinnerWidth.getValue().toString());
				int height = new Integer(spinnerHeight.getValue().toString());
				// try to create new file
				if (!DashAppController.getEventManager().createEmptyDashboard(width, height, nameInput.getText())) {
					SimpleDialogs.report("Unable to create new file. Check write permissions.");
				}
			}
		}
	}

}

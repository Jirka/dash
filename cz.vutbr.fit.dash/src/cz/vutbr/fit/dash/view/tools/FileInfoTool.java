package cz.vutbr.fit.dash.view.tools;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import cz.vutbr.fit.dash.view.IComponent;
import cz.vutbr.fit.dash.view.SideBar;

/**
 * View which contains dashboard file info.
 * 
 * @author Jiri Hynek
 *
 */
public class FileInfoTool extends AbstractGUITool implements IGUITool, IComponent {

	private JFormattedTextField text;

	public FileInfoTool() {
		text = new JFormattedTextField();
	}
	
	@Override
	public void provideSidebarItems(SideBar sideBar) {
		sideBar.addItem("file", getComponent());
	}
	
	@Override
	public JComponent getComponent() {
		return text;
	}

}

package cz.vutbr.fit.dash.view;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import cz.vutbr.fit.dash.view.tools.IGUITool;
import com.java2s.tutorials.verticallabelui.VerticalLabelUI;

/**
 * Sidebar.
 * 
 * @author Jiri Hynek
 *
 */
public class SideBar implements IComponent {
	
	/**
	 * Main component
	 */
	JTabbedPane tabbedPane;
	
	@Override
	public JComponent getComponent() {
		return tabbedPane;
	}
	
	public SideBar(List<IGUITool> plugins) {
		tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
		
		// generic menu items
        for (IGUITool plugin : plugins) {
        	plugin.provideSidebarItems(this);
		}
	}

	/**
	 * Adds new sidebar item.
	 * 
	 * @param label
	 * @param component
	 */
	public void addItem(String label, JComponent component) {
		int i = tabbedPane.getTabCount();
		tabbedPane.add(label, component);
		JLabel labTab = new JLabel("  " + label + "  ");
		labTab.setUI(new VerticalLabelUI(false));
		tabbedPane.setTabComponentAt(i, labTab);
	}

}

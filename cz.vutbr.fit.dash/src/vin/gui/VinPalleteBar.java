package vin.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * Class which contains palette.
 * 
 * @author jurij
 *
 */
public class VinPalleteBar extends VinActions implements VinIComponent {
	
	protected JToolBar bar;
	protected List<JToggleButton> listOfButtons;
	protected ButtonGroup group;
	
	/**
	 * Creates palette.
	 */
	public VinPalleteBar() {
		
		bar = new JToolBar(JToolBar.VERTICAL);
		listOfButtons = new ArrayList<JToggleButton>();
		group = new ButtonGroup();
		
		VinActions actions = ViewGui.getActions();
		
		// PROFILE //
        JToggleButton btn = addTButton("Tool1", "1", actions.getActionPallete(), 0);
        btn.setSelected(true);
        addTButton("Tool2", "2", actions.getActionPallete(), 0);
        addTButton("Tool3", "3", actions.getActionPallete(), 0);
        
        bar.addSeparator();
        
        // PLAY //
        addTButton("Tool4", "4", actions.getActionPallete(), 0);
        addTButton("Tool5", "5", actions.getActionPallete(), 0);
        addTButton("Tool6", "6", actions.getActionPallete(), 0);
        
        bar.addSeparator();
        
        addButton("GreyScale", "G", actions.getActionGrayScale(), 0);
        addButton("Adaptive treshold", "A", actions.getActionAdaptive(), 0);
        addButton("Adaptive treshold", "B", actions.getActionAdaptive(), 1);
	}	

	/**
	 * Adds new toggle button to palette.
	 * 
	 * @param tooltip
	 * @param text
	 * @param action
	 * @param mnemonic
	 * @return button
	 */
	private JToggleButton addTButton(String tooltip, String text, Action action, int mnemonic) {
    	
    	JToggleButton button = new JToggleButton();
    	//button.setAction(action);
    	button.setText(text);
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	listOfButtons.add(button);
    	group.add(button);
    	bar.add(button);
    	
    	return button;
    }
	
	/**
	 * Adds new button to palette.
	 * 
	 * @param tooltip
	 * @param text
	 * @param action
	 * @param mnemonic
	 * @return button
	 */
	private JButton addButton(String tooltip, String text, Action action, int mnemonic) {
    	
    	JButton button = new JButton();
    	button.setAction(action);
    	button.setText(text);
    	button.setToolTipText(tooltip);
    	button.setMnemonic(mnemonic);
    	bar.add(button);
    	
    	return button;
    }
    
	@Override
    public JComponent getWidget(){
        return bar;
    }
    
    /**
     * Returns palette selection.
     * 
     * @return selection
     */
    public int getSelection() {
    	
    	int i = 0;
    	
    	for(AbstractButton a : listOfButtons) {
    		if(a.isSelected()) return i;
    		i++;
    	}
    	
    	return -1;
    }

}

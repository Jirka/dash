package vin.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import vin.gui.ViewGui;
import vin.gui.VinSurface;
import vin.gui.VinToolBar;

/**
 * Class which contains new file dialog.
 * 
 * @author jurij
 *
 */
@SuppressWarnings("serial")
public class VinNewFileDialog extends JFrame implements ActionListener {
	
	protected boolean timerEnabled;
	protected JButton btnOK, btnCancel, btnColor;
	protected JSpinner spinnerWidth, spinnerHeight, spinnerTime;
	
	/**
	 * Creates new file dialog.
	 */
	public VinNewFileDialog() {
	    super("VIN 2012 | Create new picture.");
	    
	    // set position (in the middle of screen) //
		Toolkit nastroje = this.getToolkit();
	    Dimension screenSize = nastroje.getScreenSize();
		this.setBounds((screenSize.width/2-150), screenSize.height/2-75, 300, 150);
		
	    JPanel panel = new JPanel(new FlowLayout());
	    
	    // label //
	    JLabel text = new JLabel("Set size of new image.");
	    
	    // panel width //
	    JPanel panelWidth = new JPanel(new FlowLayout());
	    JLabel labelWidth = new JLabel("width:");
	    spinnerWidth = new JSpinner();
	    spinnerWidth.setValue(640);
	    spinnerWidth.setPreferredSize(new Dimension(60, 20));
	    panelWidth.add(labelWidth);
	    panelWidth.add(spinnerWidth);
	    
	    // panel height //
	    JPanel panelHeight = new JPanel(new FlowLayout());
	    JLabel labelHeight = new JLabel("height:");
	    spinnerHeight = new JSpinner();
	    spinnerHeight.setValue(480);
	    spinnerHeight.setPreferredSize(new Dimension(60, 20));
	    panelHeight.add(labelHeight);
	    panelHeight.add(spinnerHeight);
	    
	    // panel height //
	    JPanel panelTime = new JPanel(new FlowLayout());
	    JLabel labelTime = new JLabel("time (ms):");
	    spinnerTime = new JSpinner();
	    spinnerTime.setValue(100);
	    spinnerTime.setPreferredSize(new Dimension(60, 20));
	    panelTime.add(labelTime);
	    panelTime.add(spinnerTime);
	    
	    // panel color //
	    JPanel panelColor = new JPanel(new FlowLayout());
	    JLabel labelColor = new JLabel("color:");
	    btnColor = new JButton();
	    btnColor.setPreferredSize(new Dimension(60, 20));
	    btnColor.setBackground(ViewGui.getVinScrollPane().getVinSurface().getImageBackground());
	    btnColor.addActionListener(new ActionListener() {
			
			@SuppressWarnings("static-access")
            @Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame frame = new JFrame();
	            frame.setBounds(200, 200, 500, 350);
	        	JColorChooser chooser = new JColorChooser();
	        	Color color1 = ViewGui.getVinScrollPane().getVinSurface().getImageBackground();
	        	color1 = chooser.showDialog(frame, "Choose color", color1);
	        	if(color1 != null) {
	        		btnColor.setBackground(color1);
	        		ViewGui.getVinScrollPane().getVinSurface().setImageBackground(color1);
	        	}
				
			}
		});
	    panelColor.add(labelColor);
	    panelColor.add(btnColor);
	    
	    // add sub-panels //
	    panel.add(panelWidth);
	    panel.add(panelHeight);
	    panel.add(panelTime);
	    panel.add(panelColor);
	    
	    // OK, CANCEL //
	    JPanel panelBottom = new JPanel(new FlowLayout());
	    btnOK = new JButton("OK");
	    btnOK.addActionListener(this);
	    btnCancel = new JButton("Cancel");
	    btnCancel.addActionListener(this);
	    panelBottom.add(btnOK);
	    panelBottom.add(btnCancel);	    
	    
	    this.add(text, BorderLayout.NORTH);
	    this.add(panel, BorderLayout.CENTER);
	    this.add(panelBottom, BorderLayout.SOUTH);
    }

	@Override
    public void actionPerformed(ActionEvent e) {
	    
		if(e.getSource() == btnOK) {
			
			if(timerEnabled) {
				timerEnabled = false;
				ViewGui.getVinToolbar().enablePlay(true);
			}
			
			
			int width = Integer.parseInt(spinnerWidth.getValue().toString());
			int height = Integer.parseInt(spinnerHeight.getValue().toString());
			int time = Integer.parseInt(spinnerTime.getValue().toString());
			
			ViewGui.getVinScrollPane().getVinSurface().newImage(width, height, time);
			
			this.setVisible(false);
		} else if (e.getSource() == btnCancel) {
			this.setVisible(false);
		}
    }
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		// stop timer if new file dialog opened //
		VinSurface surface = ViewGui.getVinScrollPane().getVinSurface();
		VinToolBar toolbar = ViewGui.getVinToolbar();
		
		if(visible) {
			spinnerTime.setValue(ViewGui.getVinScrollPane().getVinSurface().getTime());
			timerEnabled = surface.isTimeEnabled();
			
			if(timerEnabled) {
				surface.setTimeEnabled(false);
				toolbar.enablePlay(false);
			}
		} else {
			if(timerEnabled) {
				surface.setTimeEnabled(true);
			}
		}
	}

}

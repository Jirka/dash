package cz.vutbr.fit.dashapp.view.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AnalysisPreferenceWindow {
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	public void open() {
		JFrame frame = new JFrame("Manage analysis settings");
		
		Toolkit toolkit = frame.getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setBounds((screenSize.width/2-WIDTH/2), screenSize.height/2-HEIGHT/2, WIDTH, HEIGHT);
		
		JTextArea area = new JTextArea("ahoj");
		area.setEditable(false);
		frame.add(new JScrollPane(area), BorderLayout.CENTER);
		frame.setVisible(true);
	}
}

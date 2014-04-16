package main.java.player;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HelpTextPanel extends JPanel {
	public static final String HELP = "random help instructions";
	private JTextArea helpText;
	
	public HelpTextPanel(){
		helpText = new JTextArea(10,40);
		initHelpInfoPanel();
		
	}
	
	private void initHelpInfoPanel(){
		helpText.setEditable(false);
		helpText.append(HELP);
		add(helpText, BorderLayout.CENTER);
	}
}


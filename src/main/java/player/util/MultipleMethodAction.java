package main.java.player.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.java.reflection.MethodAction;

/**
 * adapted from Duvall's MethodAction class
 * @author Michael Han
 *
 */
public class MultipleMethodAction implements ActionListener{
	private MethodAction[] myMethodActions;

	public MultipleMethodAction(MethodAction ... methods){
		myMethodActions = methods;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(MethodAction m: myMethodActions){
			m.actionPerformed(e);
		}
	}
}

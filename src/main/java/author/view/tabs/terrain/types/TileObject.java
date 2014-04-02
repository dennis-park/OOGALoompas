package main.java.author.view.tabs.terrain.types;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import main.java.author.view.tabs.terrain.Canvas;

public abstract class TileObject extends JButton{
	
	public TileObject() {

	}
	
	public abstract boolean canWalkOver();
	public abstract Color getBGColor();
	public abstract String getImage();

}
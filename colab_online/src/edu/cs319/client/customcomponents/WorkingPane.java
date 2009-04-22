package edu.cs319.client.customcomponents;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JEditorPane;

public class WorkingPane extends JEditorPane {

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isEditable()) {
			setBackground(Color.LIGHT_GRAY);
		}
	}
}
package app;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TestApplet extends Applet {
	
	// applet dimensions -- eventually want to make these parameters in the html code (gets passed to applet code)
	private int width = 500;
	private int height = 400;
	TextField input, output;
	
	JTextField textField = new JTextField(20);
	
	@Override
	public void init() {
		super.init();
		
		// sets size of applet
		setSize(width, height);
		
		// Construct the TextFields
	    this.input = new TextField(40);
	    this.output = new TextField(40);
	    this.output.setEditable(false);
	    Button b = new Button("Enter");
	    
	    // add elements
	    this.add(input);
	    this.add(b);
	    this.add(output);
	}
	
	public void paint(Graphics g)
	{
		g.drawString ("Hello World", 150,100);
	}
	
}

/*

class TextEnterAction implements ActionListener() {
	
	private Textfield in, out;
	
	public TextEnterAction() {
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
	}
	
}
*/
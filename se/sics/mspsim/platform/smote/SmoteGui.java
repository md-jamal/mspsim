
package se.sics.mspsim.platform.smote;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import se.sics.mspsim.chip.Leds;
import se.sics.mspsim.core.StateChangeListener;
import se.sics.mspsim.platform.AbstractNodeGUI;



public class SmoteGui extends AbstractNodeGUI {


	private final SmoteNode node;
	private boolean buttonDown = false;
	private boolean resetDown = false;
	/*
	private static final int LED_HEIGHT = 8;
	private static final int LED_WIDTH = 5;

	private static final int RED1_X = 148;
	private static final int LED_Y = 220;

	private static final Color RED_TRANS = new Color(0xf0, 0x40, 0x40, 0xa0);
	private static final Color RED_C = new Color(0xffff8000);

	private static final Rectangle LEDS_BOUNDS = new Rectangle(RED1_X - 2,
            LED_Y - 1,  RED1_X + LED_HEIGHT, LED_WIDTH);
	*/

	private static final int RED1_X = 77;
   	private static final int GREEN_X = 77;
    	private static final int RED2_X = 77;
    	private static final int LED_Y = 159;
    	private static final int LED_HEIGHT = 8;
    	private static final int LED_WIDTH = 10;

    	private static final Color GREEN_TRANS = new Color(0xf0, 0x40, 0x40, 0xa0);
    	private static final Color RED_TRANS = new Color(0xf0, 0x40, 0x40, 0xa0);

    	private static final Color GREEN_C = new Color(0xffff8000);
    	private static final Color RED_C = new Color(0xffff8000);

    	private static final Color BUTTON_C = new Color(0x60ffffff);

    	private static final Rectangle LEDS_BOUNDS = new Rectangle(RED1_X - 2,
            LED_Y - 1, RED2_X - RED1_X + LED_HEIGHT, LED_WIDTH);

	private final StateChangeListener ledsListener = new StateChangeListener() 		{
        	public void stateChanged(Object source, int oldState, int newState) 			{
	            repaint(LEDS_BOUNDS);
        	}
    	};

	public SmoteGui(SmoteNode node)
	{
		super("SmoteGui","images/smote.jpg");
		this.node = node;
	}

	protected void startGUI() {
		 System.out.println("Start GUI of smote called");
		 MouseAdapter mouseHandler = new MouseAdapter()
		 {

           	 	// For the button sensor and reset button on the Sky nodes.
	         	public void mousePressed(MouseEvent e) 
			{
		                int x = e.getX();
                 		int y = e.getY();
				System.out.println("mouse x:"+x +"and y:"+y);
                 		if (x > 100 && x < 120) 
				{
		                 	if (y > 170 && y < 185) 
					{
		                 	       buttonDown = true;
		                 	       SmoteGui.this.node.getButton().setPressed(true);
						System.out.println("User button pressed");				
		                 	       repaint(100, 185, 120, 170);
		                 	} 
					else if (y > 190 && y < 205) 
					{
		                        	resetDown = true;
						System.out.println("Reset button pressed");
						SmoteGui.this.node.getCPU().reset();
		                        	repaint(100, 205, 120, 190);
		                    	}
		                }
	            	}

            		public void mouseReleased(MouseEvent e) 
			{
                		if (buttonDown) 
				{
		                    buttonDown = false;
		                    SmoteGui.this.node.getButton().setPressed(false);
	                 	    repaint(100, 185, 120, 170);

		                }
				else if (resetDown) 
				{
		                    int x = e.getX();
		                    int y = e.getY();
                		    resetDown = false;                		   
                		}
            		}	
     		   };

       		 this.addMouseListener(mouseHandler);
		 node.getLeds().addStateChangeListener(ledsListener);
	}


	protected void stopGUI() {
		System.out.println("Stop GUI Called");
	
	}


	protected void paintComponent(Graphics g) {
        	Color old = g.getColor();
		//System.out.println("Paint Component called\n");
        	super.paintComponent(g);
	
        	// Display all active LEDs
        	Leds leds = node.getLeds();
        	int l = leds.getLeds();
		//System.out.println("Value of l is "+l);
        	
        	if ((l & 2) != 0) {
        	    g.setColor(GREEN_TRANS);
        	    g.fillOval(GREEN_X - 2, LED_Y - 1, LED_HEIGHT, LED_WIDTH);
        	    g.setColor(GREEN_C);
        	    g.fillOval(GREEN_X, LED_Y, LED_HEIGHT - 5, LED_WIDTH - 2);
        	}
        	
        	if (buttonDown) {
        	    //g.setColor(BUTTON_C);
        	   // g.fillOval(8, 236, 9, 9);
        	}
        	if (resetDown) {
        	    //g.setColor(BUTTON_C);
        	   // g.fillOval(8, 271, 9, 9);
        	}
        	g.setColor(old);
    	}	



}


package se.sics.mspsim.platform.smote;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import se.sics.mspsim.core.StateChangeListener;
import se.sics.mspsim.platform.AbstractNodeGUI;



public class SmoteGui extends AbstractNodeGUI {


	private final SmoteNode node;
	private boolean buttonDown = false;
	private boolean resetDown = false;
	
	private static final int LED_HEIGHT = 8;
	private static final int LED_WIDTH = 5;

	private static final int RED1_X = 148;
	private static final int LED_Y = 220;

	private static final Color RED_TRANS = new Color(0xf0, 0x40, 0x40, 0xa0);
	private static final Color RED_C = new Color(0xffff8000);

	private static final Rectangle LEDS_BOUNDS = new Rectangle(RED1_X - 2,
            LED_Y - 1,  RED1_X + LED_HEIGHT, LED_WIDTH);

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
                 		if (x > 180 && x < 200) 
				{
		                 	if (y > 235 && y < 255) 
					{
		                 	       buttonDown = true;
		                 	     //  WismoteGui.this.node.getButton().setPressed(true);
						System.out.println("User button pressed");				
		                 	       repaint(7, 237, 11, 13);
		                 	} 
					else if (y > 265 && y < 285) 
					{
		                        	resetDown = true;
						System.out.println("Reset button pressed");
		                        	repaint(7, 269, 11, 13);
		                    	}
		                }
	            	}

            		public void mouseReleased(MouseEvent e) 
			{
                		if (buttonDown) 
				{
		                    buttonDown = false;
		                    //WismoteGui.this.node.getButton().setPressed(false);
		                    repaint(7, 237, 11, 13);

		                }
				else if (resetDown) 
				{
		                    int x = e.getX();
		                    int y = e.getY();
                		    resetDown = false;
                		    repaint(7, 269, 11, 13);
                		    if (x > 6 && x < 19 && y > 268 && y < 282) 
				    {
		                       // WismoteGui.this.node.getCPU().reset();
                		    }
                		}
            		}	
     		   };

       		 this.addMouseListener(mouseHandler);
		 node.getLeds().addStateChangeListener(ledsListener);
	}


	protected void stopGUI() {
		System.out.println("Stop GUI Called");
	
	}
}

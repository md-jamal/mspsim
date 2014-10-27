 
 /* -----------------------------------------------------------------
 *
 * SmoteNode
 *
 * Author  : Mohammad Jamal Mohiuddin<mjmohiuddin@cdac.in>
 */


package se.sics.mspsim.platform.smote;
import java.io.IOException;
import se.sics.mspsim.chip.Button;
import se.sics.mspsim.chip.CC2520;
import se.sics.mspsim.chip.DS2411;
import se.sics.mspsim.chip.Leds;
import se.sics.mspsim.config.MSP430f2618Config;
import se.sics.mspsim.core.EmulationException;
import se.sics.mspsim.core.IOPort;
import se.sics.mspsim.core.IOUnit;
import se.sics.mspsim.core.PortListener;
import se.sics.mspsim.core.USARTListener;
import se.sics.mspsim.core.USARTSource;
import se.sics.mspsim.platform.GenericNode;
import se.sics.mspsim.ui.SerialMon;
import se.sics.mspsim.util.ArgumentManager;


public class SmoteNode extends GenericNode implements PortListener,USARTListener
{

	//public DS2411 ds2411;

	public static final int CC2520_FIFOP = 2;
    	public static final int CC2520_FIFO = 5;
    	public static final int CC2520_CCA = 6;
    	/* P2.0 - Input: SFD from CC2520 */
    	public static final int CC2520_SFD = 3;
    	/* P3.0 - Output: SPI Chip Select (CS_N) */
    	public static final int CC2520_CHIP_SELECT = 0x01;
    	/* P4.3 - Output: VREG_EN to CC2520 */
    	public static final int CC2520_VREG = 1 << 3;
    	/* P4.4 - Output: RESET_N to CC2520 */
    	public static final int CC2520_RESET = 1 << 4;


	private CC2520 radio;
	private Leds leds;
	private Button button;
	private SmoteGui gui;

	/* P8.6 - Red (left) led */
    	private static final int LEDS_CONF_RED1   = 1 << 5;
    	private static final int LEDS_RED1        = 1 << 0;
    	/* P5.5 - Green (middle) led */
   	private static final int LEDS_CONF_GREEN  = 1 << 5;
    	private static final int LEDS_GREEN       = 1 << 1;
    	/* P2.4 - Red (right) led */
    	private static final int LEDS_CONF_RED2   = 1 << 5;
    	private static final int LEDS_RED2        = 1 << 2;
	
	/*Colors of each Leds*/
    	private static final int[] LEDS = { 0xff2020, 0xff2020, 0xff2020 };


	/* P1.4 - Button */
	public static final int BUTTON_PIN = 4;
	public static final int MODE_LEDS_OFF = 0;
	public SmoteNode()
	{
		super("Smote",new MSP430f2618Config());
	        setMode(MODE_LEDS_OFF);		
	}

	public Leds getLeds() {
        	return leds;
        }


	public Button getButton() {
        	return button;
    	}

	private void setupNodePorts() {
		//ds2411 = new DS2411(cpu);Not present

		IOPort port1 = cpu.getIOUnit(IOPort.class, "P1");
        	port1.addPortListener(this);
	
		IOPort port2 = cpu.getIOUnit(IOPort.class, "P2");
        	port2.addPortListener(this);

		cpu.getIOUnit(IOPort.class, "P3").addPortListener(this);
        	cpu.getIOUnit(IOPort.class, "P4").addPortListener(this);
        	cpu.getIOUnit(IOPort.class, "P5").addPortListener(this);
		//cpu.getIOUnit(IOPort.class, "P6").addPortListener(this);
		//cpu.getIOUnit(IOPort.class, "P7").addPortListener(this);
        	cpu.getIOUnit(IOPort.class, "P8").addPortListener(this);
		
		IOUnit usart0 = cpu.getIOUnit("USCI B1");
        	if (usart0 instanceof USARTSource) {

           		radio = new CC2520(cpu);
            		radio.setGPIO(1, port1, CC2520_FIFO);
		        radio.setGPIO(3, port1, CC2520_CCA);
		        radio.setGPIO(2, port1, CC2520_FIFOP);
		        radio.setGPIO(4, port1, CC2520_SFD);

		        ((USARTSource) usart0).addUSARTListener(this);
	        } else {
         	   throw new EmulationException("Could not setup wismote mote - 				missing USCI B1");
        	}

		leds = new Leds(cpu, LEDS);
	        button = new Button("Button", cpu, port1, BUTTON_PIN, true);
	
        	IOUnit usart = cpu.getIOUnit("USCI A1");
        	if (usart instanceof USARTSource) {
		//	((USARTSource) usart).addUSARTListener(this);
        	    registry.registerComponent("serialio", usart);
        	}

	}

	public void setupNode() {
		System.out.println("Setup Node called");
		// create a filename for the flash file
	        // This should be possible to take from a config file later!
        	String fileName = config.getProperty("flashfile");
        	if (fileName == null) {
        	    fileName = firmwareFile;
        	    if (fileName != null) {
        	        int ix = fileName.lastIndexOf('.');
        	        if (ix > 0) {
        	            fileName = fileName.substring(0, ix);
        	        }
        	     fileName = fileName + ".flash";
            	}
        	}
	        if (true) System.out.println("Using flash file: " + (fileName == null ? "no file" : fileName));

		setupNodePorts();

		if (!config.getPropertyAsBoolean("nogui", true)) {
           	 setupGUI();

            // Add some windows for listening to serial output
            IOUnit usart = cpu.getIOUnit("USCI A1");
            if (usart instanceof USARTSource) {
                SerialMon serial = new SerialMon((USARTSource)usart, "USCI A1 Port Output");
                registry.registerComponent("serialgui", serial);
            }
        }

	}

	public void setupGUI() {
        	if (gui == null) {
            	gui = new SmoteGui(this);
            	registry.registerComponent("nodegui", gui);
        	}
    	}
	public void portWrite(IOPort source, int data) {
		
		switch (source.getPort()) {
        	case 1:
	            //ds2411.dataPin((data & DS2411_DATA) != 0);
        	    break;
        	case 2:
        	   leds.setLeds(LEDS_RED2, (data & LEDS_CONF_RED2) == 0 && 				(source.getDirection() & LEDS_CONF_RED2) != 0);
	            break;
        	case 3:
        	     //Chip select = active low...
        	   // radio.setChipSelect((data & CC2520_CHIP_SELECT) == 0);
        	    break;
        	case 4:
        	    //radio.portWrite(source, data);
        	    //flash.portWrite(source, data);
        	    //radio.setVRegOn((data & CC2520_VREG) != 0);
        	    break;
        	case 5:
			//System.out.println("portWrite called for port5");
		     leds.setLeds(LEDS_GREEN, (data & LEDS_CONF_GREEN) == 0 && 				(source.getDirection() & LEDS_CONF_GREEN) != 0);
        	    
	            break;
        	case 8:
        	    leds.setLeds(LEDS_RED1, (data & LEDS_CONF_RED1) == 0 && 				(source.getDirection() & LEDS_CONF_RED1) != 0);
	            break;
        	}
    
        }


	public void dataReceived(USARTSource source, int data) {
		System.out.println("Data received called"+data);
	}

	public int getModeMax() {
		System.out.println("getModeMax called");
	        return 0;
        }


	public static void main(String[] args) throws IOException {
		SmoteNode node = new SmoteNode();
		ArgumentManager config = new ArgumentManager();
		config.handleArguments(args);
		node.setupArgs(config);
	}

}

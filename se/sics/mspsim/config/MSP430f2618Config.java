 /* -----------------------------------------------------------------
 *
 * SmoteNode
 *
 * Author  : Mohammad Jamal Mohiuddin<mjmohiuddin@cdac.in>
 */


package se.sics.mspsim.config;

public class MSP430f2618Config extends MSP430f2617Config {




	public MSP430f2618Config(){
	 /* MSP430f2618 is similar to MSP430f2617 but the memory map is different */
         /*Table 8:Memory Organization Datasheet */

	 /* Informational Memory start and size */       
	 infoMemConfig(0x1000, 128 * 2);
	 /* Flash Configuration Memory start and Size */
	 mainFlashConfig(0x3100, 116 * 1024);
         /* RAM Memory start and Size */
	 ramConfig(0x1100, 8 * 1024);
	 /* Mirrored Memory start,Size and address */
 	 ramMirrorConfig(0x200, 2 * 1024, 0x1100);
    


	}









}

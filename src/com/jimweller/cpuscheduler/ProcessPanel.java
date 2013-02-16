package com.jimweller.cpuscheduler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
//import Process;
import java.awt.geom.*;

/**
 * A process panel is a thin tall (about 115x100) rectangle that consists
 * of a cpu meter and a priority indicator. The burst panel shows if a process
 * is arrived or active. It also shows a progress bar relating a processes initial
 * cpu burst and it's remaining burst times. The priority at the bottom shows the 
 * weight a process is given in some scheduling algorithms
 */
class ProcessPanel extends JPanel{

    /** The process this panel sharkfishes from */
    Process proc;

    /** The width of the process panel */
    static final int PPWIDTH  = 10;

    /** The height of the process panel */
    static final int PPHEIGHT = 115;

    /** The height you want the meters drawn. I do a 1:1 ratio with my maximum burst. */
    static final int BARHEIGHT = 100;

    /** Some pretty colors to draw with. */
    Color burstColor,
	initBurstColor=Color.darkGray,
	unarrivedColor,
	lblColor;
    
    /** The label to show the priority. */
    JLabel priLbl;

    /** Do you want to see unarrived processes? Look into the future.  */
    static boolean showHidden=false;
        
   
    /** Default constructor. Generates its own process. */
    ProcessPanel(){
	proc = new Process();
	initPanel();
    }

    /**
     * Articulate constructor.
     * param p the process to base this panel on. 
     */
    ProcessPanel( Process p){
	proc = p;
	initPanel();
    }

    /**
     * Build the panel
     */
    void initPanel(){
	setAlignmentX(Component.LEFT_ALIGNMENT);
	setLayout(new BorderLayout());
	
	priLbl = new JLabel(""+ (int)proc.getPriorityWeight());
	priLbl.setToolTipText("Once a process has arrived this shows its"+
			      " priority. (0 High and 9 Low)");
	priLbl.setHorizontalAlignment(SwingConstants.CENTER);


	//Font jf =  new Font("Dialog",Font.PLAIN,10);
	//priLbl.setFont( jf );
	//priLbl.setOpaque(true);

	setSize(PPWIDTH,PPHEIGHT);
	setBackground(Color.white);
	setOpaque(true);
	add(priLbl,"South");
    }

    /**
     * If the process is done remove it. Otherwise update the burst meter.
     */
    public void paintComponent(Graphics g){
	super.paintComponent(g);
	if ( proc.isFinished() == true){
	    setVisible(false);
	}
	else {
	    DrawBursts(g);
	    //	    setVisible(true);
	}
    }

    
    /**
     * Draw the burst panel. Draw remaining burst over finished burst.
     * Draw the active process in bright color.
     */
    void DrawBursts(Graphics g){
	int initBurstHeight=0,burstHeight=0;
	int width=0;

	initBurstHeight = (int) proc.getInitBurstTime();
	burstHeight = (int) proc.getBurstTime();
	width  = (int) PPWIDTH-2; // off by one error in swing?
    	


	lblColor = ( proc.isArrived()  ? Color.black :
		     (showHidden ? Color.darkGray : Color.white) );

	initBurstColor = ( proc.isArrived() ? Color.black : Color.darkGray );

	burstColor  = (proc.isArrived() ) ? 
	    (proc.isActive() == true ? Color.red : new Color(0,0,173) ):
	    (showHidden ? Color.darkGray : Color.white) ;


	priLbl.setForeground( lblColor );
	//priLbl.setBackground( proc.isActive() ? Color.red : Color.white );


	if( proc.isArrived() ){
	    g.setColor(initBurstColor);
	    g.drawRect(0,BARHEIGHT-initBurstHeight,width,initBurstHeight);
	    g.setColor(burstColor);
	    g.fillRect(1,BARHEIGHT-burstHeight+1,width-1,burstHeight-1);
	}
	else if( showHidden ){
	    g.setColor(initBurstColor);
	    g.drawRect(0,BARHEIGHT-initBurstHeight,width,initBurstHeight);
	}
	
   }


    /**
     * Get the value of proc.
     * @return Value of proc.
     */
    public Process getProc() {return proc;}

    
    /**
     * Set the value of proc.
     * @param v  Value to assign to proc.
     */
    public void setProc(Process  v) {this.proc = v;}
    
	
    public Dimension getPreferredSize(){
	return ( new Dimension(PPWIDTH,PPHEIGHT));
    }
   
    
    /**
       * Get the value of showHidden.
       * @return Value of showHidden.
       */
    public static boolean getShowHidden() {return showHidden;}
    
    /**
       * Set the value of showHidden.
       * @param v  Value to assign to showHidden.
       */
    public static void setShowHidden(boolean  v) { showHidden = v;}

} // ENDS  ProcessPanel


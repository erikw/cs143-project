package com.jimweller.cpuscheduler;

import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.net.*;

/** 
 * CPUSchedulerFrameForApplet is a JFrame that contains and represents a CPUScheduler object. One can
 * load random and predetermined data sets, run simulations from a GUI and watch an animation
 * of the process thanks to to a time signal responded to using the ActionListener interface.
 */
public class CPUSchedulerFrameForApplet extends JFrame implements ActionListener {

    
    CPUScheduler cpu;

    JCheckBox startCB;
    ImageIcon playPic, pausePic, pressPic;


    JMenuBar menuBar;
    JMenu fileMenu, algorithmMenu, optionsMenu, speedMenu;
    JMenuItem newMI, openMI, resetMI, saveMI,quitMI;
    JRadioButtonMenuItem fps1MI,fps10MI,fps20MI,fps30MI,fps40MI,fps50MI,
	fps60MI,fps70MI,fps80MI,fps90MI,fps100MI;
    JRadioButtonMenuItem  fcfsRB,sjfRB,rrRB,priRB;
    JCheckBoxMenuItem     preemptCB,priCB,showHiddenCB;


    JLabel statusBar,algolLbl;
    StatsPanel waitSP, turnSP,responseSP;
    ClockPanel cpuTimePanel;

    JSlider delaySlider, lengthSlider, countSlider, quantumSlider;


    int frameNumber = -1;
    int fps = 30;
    Timer timer;
    boolean frozen = true;

    JPanel contentPane, queuePanel, buttonPanel;

    String fileName="";
	


    /**
     * Default constructor, builds and displays a random CPUScheduler object.
     */
    public CPUSchedulerFrameForApplet(){

	//setup animation and simulation
	cpu = new CPUScheduler();
	int delay = (fps > 0) ? (1000 / fps) : 100;
        timer = new Timer(delay,this);
        timer.setCoalesce(false); // don't combine queued events
	timer.setInitialDelay(0);
	
	// setup frame
	setTitle("CPU Scheduler Simulation");
	setSize(790,390);
	
        //URL logoUrl = getClass().getResource("pics/cpu.jpg");
	//setIconImage((new ImageIcon(logoUrl)).getImage());
	
	buildButtons();
	queuePanel = new JPanel();
	buildMenus();
	buildStatusPanels();
	fillQueuePanel();
	//buildFileDialog();
	updateReadouts();

	Container masterPanel = getContentPane() ;
	masterPanel.setLayout(new BoxLayout(masterPanel,BoxLayout.Y_AXIS));

	JPanel topRow = new JPanel();
	topRow.setLayout(new BorderLayout());
	topRow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	JPanel middleRow = new JPanel();
	middleRow.setLayout(new FlowLayout(FlowLayout.CENTER));
	middleRow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	JPanel bottomRow = new JPanel();
	bottomRow.setLayout(new BoxLayout(bottomRow,BoxLayout.Y_AXIS));
	bottomRow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


	//topRow.add(statusBar,"North");
	topRow.add(queuePanel,"North");

	middleRow.add(cpuTimePanel);
	middleRow.add(responseSP);
	middleRow.add(turnSP);
	middleRow.add(waitSP);

	bottomRow.add(middleRow,"Center");
	bottomRow.add(startCB,"South");


	masterPanel.add(topRow);
	//masterPanel.add(middleRow);
	masterPanel.add(bottomRow);

	addWindowListener(
			  new WindowAdapter() {
				  public void windowClosing(WindowEvent e) { 
				      System.exit(0); 
				  } 
			  }
	);
	setVisible(true);
    }


    /**
     * Remove all panels from the box that contains the graphical representation of 
     * the ready queue
     */
    public void emptyQueuePanel(){
	queuePanel.removeAll();
	
    }

    /**
     * Redisplay all hidden ProcessPanels
     */
    public void resetQueuePanel(){
	ProcessPanel p;
	int num = queuePanel.getComponentCount();
	for(int i=0; i < num;i++){
	    p = (ProcessPanel) queuePanel.getComponent(i);
	    p.setVisible(true);
	}
    }

    /** 
     * Display the jobs from the CPUScheduler on a blank jobQueue
     */
    public void fillQueuePanel(){
	Vector v = cpu.getJobs();
 	queuePanel.setBackground(Color.white);
 	queuePanel.setOpaque(true);
	queuePanel.setSize(140,340);
	queuePanel.setPreferredSize(new Dimension(640,140));
	queuePanel.setMinimumSize(new Dimension(640,130));
	FlowLayout flay = new FlowLayout(FlowLayout.LEFT);
	queuePanel.setLayout(flay);
	//queuePanel.setMaximumSize(new Dimension(640,130));
	for( int i = 0; i < v.size() ; i++){
	    ProcessPanel p = new ProcessPanel( (Process) v.get(i) );
	    queuePanel.add(p,"Left");
	}
	queuePanel.revalidate();
    }


    /** 
     *Setup the panels used to display status. CPU time, wait time, response time and turnaround time 
     */
    void buildStatusPanels(){
	statusBar = new JLabel("");
	statusBar.setBorder( BorderFactory.createEmptyBorder(10,0,0,0) );
	statusBar.setAlignmentX(Component.LEFT_ALIGNMENT);

	
	cpuTimePanel = new ClockPanel("CPU");
	cpuTimePanel.setStats(0,0,0);
	cpuTimePanel.setToolTipText("The \"real\" time on the CPU clock");

	waitSP = new StatsPanel("Wait");
	waitSP.setStats(0,0,0,0);
	waitSP.setToolTipText("Wait time is the total amount of"+
			      " time a ready to run process is spent"+
			      "not running");

	turnSP = new StatsPanel("Turnaround");
	turnSP.setStats(0,0,0,0);
	turnSP.setToolTipText("Turnaround time is the total amount"+
			      " of time that a process is either "+
			      "running or waiting. Its lifetime.");

	responseSP = new StatsPanel("Response");
	responseSP.setStats(0,0,0,0);
	responseSP.setToolTipText("Response time is the amount of time"+
				  " it takes for the CPU to begin execution"+
				  " of a process after it has entered the"+
				  " ready queue.");


	algolLbl = new JLabel("FCFS",JLabel.CENTER);
	algolLbl.setToolTipText("<html>"+
				"This tells which algorithm your using." +
				"<UL>"+
				"<LI><B>FCFS</B> First Come First Serve</LI>"+
				"<LI><B>SJF</B> Shortest job first</LI>"+
				"<LI><B>RR</B> Round Robin</LI>"+
				"<LI><B>PRI</B> Priority Weighting</LI>"+
				"</ul>"+
				"</html>");
	algolLbl.setBorder( BorderFactory.createEmptyBorder(0,5,5,20) );

    }


    /**
     * A long gory event handler. Checks the origin of the event and responds accordingly.
     */
    public void actionPerformed(ActionEvent e){

	if( e.getSource() == startCB ){
	    if(frozen == false){
		frozen = true;
		stopAnimation(); 
		//startCB.setSelected(false);
	    }
	    else{
		frozen = false;
		startAnimation();
		//startCB.setSelected(true);
	    }
	}
	else if( e.getSource() == timer ){
	    if ( cpu.nextCycle() == true ){
		updateReadouts();
	    }
	    else{
		stopAnimation();
		startCB.setSelected(false);
	    } 
	    repaint();
	}
	else if( e.getSource() == fcfsRB){
	    //cpu.setAlgorithm(new FCFSSchedulingAlgorithm());
	    priCB.setEnabled(false);
	    //preemptCB.setEnabled(false);
	    algolLbl.setText("FCFS");
	}
	else if( e.getSource() == rrRB){
	    //cpu.setAlgorithm(new RoundRobinSchedulingAlgorithm());
	    priCB.setEnabled(true);
	    preemptCB.setEnabled(false);
	    algolLbl.setText("RR");
	}
	else if( e.getSource() == sjfRB){
	    //cpu.setAlgorithm(new SJFSchedulingAlgorithm());
	    priCB.setEnabled(false);
	    preemptCB.setEnabled(true);
	    algolLbl.setText("SJF");
	}
	else if( e.getSource() == priRB){
	    //cpu.setAlgorithm(new PrioritySchedulingAlgorithm());
	    priCB.setEnabled(false);
	    preemptCB.setEnabled(true);
	    algolLbl.setText("PRI");
	}
	else if( e.getSource() == priCB){
	    cpu.setPriority( (! cpu.getPriority()) );
	}
	else if( e.getSource() == preemptCB){
	    //cpu.setPreemption(  preemptCB.getState() );
	}
	else if( e.getSource() == showHiddenCB){
	    ProcessPanel.setShowHidden( showHiddenCB.getState() );
	    repaint();
	}
	else if( e.getSource() == newMI){
	    //int algo = cpu.getAlgorithm();
	    //cpu = new CPUScheduler();
	    //cpu.setAlgorithm(algo);
	    cpu.buildRandomQueue();
	    emptyQueuePanel();
	    fillQueuePanel();
	    updateReadouts();
	    repaint();
	}
/*	else if( e.getSource() == openMI){
	    openFileDialog.resetChoosableFileFilters();
	    openFileDialog.setFileFilter(openFilter);
	    int returnVal = openFileDialog.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) { 
	        File fileName=openFileDialog.getSelectedFile();
	        cpu = new CPUScheduler(fileName);
		emptyQueuePanel();
	        fillQueuePanel();
		updateReadouts();
	        repaint();
	    } 
	}
	else if( e.getSource() == saveMI){
	    openFileDialog.resetChoosableFileFilters();
	    openFileDialog.setFileFilter(saveFilter);
	    int returnVal = openFileDialog.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) { 
	        try{
		    String fileName =  openFileDialog.getCurrentDirectory().getName() +
			System.getProperty("file.separator") +
			openFileDialog.getSelectedFile().getName();
		    int fnLen = fileName.length();
		    if( !((fileName.substring(fnLen-4)).equals(".csv")))
			fileName += ".csv";
		    System.out.println(fileName);
		    File file= new File( fileName );
		    PrintWriter ostream = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		    cpu.printCSV(ostream);
		    ostream.flush();
		    ostream.close();
		}
		catch(IOException saveIOE){ }
	    } 
	    
	}
*/
	else if( e.getSource() == resetMI){
	    cpu.restore();
	    resetQueuePanel();
	    updateReadouts();
	    repaint();
	}
	else if( e.getSource() == quitMI){
	    stopAnimation();
	    dispose();
	    System.exit(0);
	}
	else if( e.getSource() == fps1MI){
	    setFPS(1);
	}
	else if( e.getSource() == fps10MI){
	    setFPS(10);
	}
	else if( e.getSource() == fps20MI){
	    setFPS(20);
	}
	else if( e.getSource() == fps30MI){
	    setFPS(30);
	}
	else if( e.getSource() == fps40MI){
	    setFPS(40);
	}
	else if( e.getSource() == fps50MI){
	    setFPS(50);
	}
	else if( e.getSource() == fps60MI){
	    setFPS(60);
	}
	else if( e.getSource() == fps70MI){
	    setFPS(70);
	}
	else if( e.getSource() == fps80MI){
	    setFPS(80);
	}
	else if( e.getSource() == fps90MI){
	    setFPS(90);
	}
	else if( e.getSource() == fps100MI){
	    setFPS(100);
	}
    }

    /**
     *Invoked by the browser only.  invokeLater not needed
     *because startAnimation can be called from any thread.
     */
    public void start() {
        startAnimation();
    }

    /**
     * Invoked by the browser only.  invokeLater not needed
     * because stopAnimation can be called from any thread.
     */
    public void stop() {
        stopAnimation();
    }

    /**Can be invoked from any thread.*/
    public synchronized void startAnimation() {
        if (frozen) { 
            //Do nothing.  The user has requested that we 
            //stop changing the image.
        } else {
            //Start animating!
            if (!timer.isRunning()) {
                timer.start();
            }
        }
    }

    /**Can be invoked from any thread.*/
    public synchronized void stopAnimation() {
        //Stop the animating thread.
        if (timer.isRunning()) {
            timer.stop();
        }
    }


    /** Update the status displays (but not the scheduling queue) */
    void updateReadouts(){
	cpuTimePanel.setStats( (int)cpu.getTotalTime(),
			       (int)cpu.getIdleTime(),
			       (int)cpu.getBusyTime());
	waitSP.setStats( cpu.getMinWait(),
			 cpu.getMeanWait(),
			 cpu.getMaxWait(),
			 cpu.getStdDevWait());
	responseSP.setStats( cpu.getMinResponse(),
			     cpu.getMeanResponse(),
			     cpu.getMaxResponse(),
			     cpu.getStdDevResponse());
	turnSP.setStats( cpu.getMinTurn(),
			 cpu.getMeanTurn(),
			 cpu.getMaxTurn(),
			 cpu.getStdDevTurn());
    }   


    /** Setup an open and save dialog for later use. */
    /*void buildFileDialog(){
	openFileDialog = new JFileChooser(".");
	
	openFilter = new BetterFileFilter("");
	openFilter.addExtension("dat");
	openFilter.setDescription("Process Data");

	saveFilter = new BetterFileFilter("");
	saveFilter.addExtension("csv");
	saveFilter.setDescription("Comma Seperated Values");

    }
*/

    
    /** Gory! Build all the menus for the application. */
    void buildMenus(){

	// main menu bar
	menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	


	// Build file menu
	fileMenu = new JMenu("Sets");

	newMI = new JMenuItem("New random source");
	newMI.addActionListener(this);
	fileMenu.add(newMI);

	openMI = new JMenuItem("Open Data Source...");
	openMI.addActionListener(this);
	//fileMenu.add(openMI);

	saveMI = new JMenuItem("Save Statistics...");
	saveMI.addActionListener(this);
	//fileMenu.add(saveMI);

	resetMI = new JMenuItem("Reset current source");
	resetMI.addActionListener(this);
	fileMenu.add(resetMI);

	quitMI  = new JMenuItem("Quit");
	quitMI.addActionListener(this);
	fileMenu.add(quitMI);

	menuBar.add(fileMenu);


	
	// Build options menu
	optionsMenu = new JMenu("Options");
	// Algorithms SubMenu
	algorithmMenu = new JMenu("Algorithm");


	ButtonGroup algogroup = new ButtonGroup();

	fcfsRB = new JRadioButtonMenuItem("First Come First Serve");
	fcfsRB.setSelected(true);
	fcfsRB.setToolTipText("First Come First Serve scheduling");
	algogroup.add(fcfsRB);
	fcfsRB.addActionListener(this);
	algorithmMenu.add(fcfsRB);

	sjfRB = new JRadioButtonMenuItem("Shortest Job First");
	sjfRB.setToolTipText("Shortest job first scheduling");
	algogroup.add(sjfRB);
	sjfRB.addActionListener(this);
	algorithmMenu.add(sjfRB);

	rrRB = new JRadioButtonMenuItem("Round Robin");
	rrRB.setToolTipText("Round Robin Scheduling");
	algogroup.add(rrRB);
	rrRB.addActionListener(this);
	algorithmMenu.add(rrRB);

	priRB = new JRadioButtonMenuItem("Priority");
	priRB.setToolTipText("Priority weighted scheduling");
	priRB.addActionListener(this);
	algogroup.add(priRB);
	algorithmMenu.add(priRB);

	optionsMenu.add(algorithmMenu);

	// Simulation speed submenu
	speedMenu = new JMenu("Speed");

	speedMenu.setToolTipText("Set animation rate / cpu clock");
	ButtonGroup bg = new ButtonGroup();

	fps1MI = new JRadioButtonMenuItem("1 fps");
	fps1MI.setToolTipText("Set animation rate / cpu clock to ");
	bg.add(fps1MI);
	fps1MI.addActionListener(this);
	speedMenu.add(fps1MI);

	fps10MI = new JRadioButtonMenuItem("10 fps");
	fps10MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps10MI);
	fps10MI.addActionListener(this);
	speedMenu.add(fps10MI);

	fps20MI = new JRadioButtonMenuItem("20 fps");
	fps20MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps20MI);
	fps20MI.addActionListener(this); 
	speedMenu.add(fps20MI);

	fps30MI = new JRadioButtonMenuItem("30 fps",true);
	fps30MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps30MI);
	fps30MI.addActionListener(this); 
	speedMenu.add(fps30MI);

	fps40MI = new JRadioButtonMenuItem("40 fps");
	fps40MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps40MI);
	fps40MI.addActionListener(this); 
	speedMenu.add(fps40MI);

	fps50MI = new JRadioButtonMenuItem("50 fps");
	fps50MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps50MI);
	fps50MI.addActionListener(this); 
	speedMenu.add(fps50MI);

	fps60MI = new JRadioButtonMenuItem("60 fps");
	fps60MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps60MI);
	fps60MI.addActionListener(this); 
	speedMenu.add(fps60MI);

	fps70MI = new JRadioButtonMenuItem("70 fps");
	fps70MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps70MI);
	fps70MI.addActionListener(this); 
	speedMenu.add(fps70MI);

	fps80MI = new JRadioButtonMenuItem("80 fps");
	fps80MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps80MI);
	fps80MI.addActionListener(this); 

	fps90MI = new JRadioButtonMenuItem("90 fps");
	fps90MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps90MI);
	fps90MI.addActionListener(this); 
	speedMenu.add(fps90MI);

	fps100MI = new JRadioButtonMenuItem("100 fps");
	fps100MI.setToolTipText("Set animation rate / cpu clock");
	bg.add(fps100MI);
	fps100MI.addActionListener(this);
	speedMenu.add(fps100MI);

	optionsMenu.add(speedMenu);
	optionsMenu.addSeparator();

	// Priority menu option
	priCB = new JCheckBoxMenuItem("Prioritize");
	priCB.setToolTipText("Use priority scheduling for"+
			     " the Round Robin algorithm");
	priCB.setEnabled(false);
	priCB.addActionListener(this);
	optionsMenu.add(priCB);
	// Preemptive menu option
	preemptCB = new JCheckBoxMenuItem("Preemption");
	preemptCB.setToolTipText("Preempt running process"+
				 " in the SJF and Priority algorithms.");
	preemptCB.setEnabled(false);
	preemptCB.addActionListener(this);
	optionsMenu.add(preemptCB);
	
	// Show hidden processes menu option
	showHiddenCB = new JCheckBoxMenuItem("Show hidden",false);
	showHiddenCB.addActionListener(this);
	optionsMenu.add(showHiddenCB);

	menuBar.add(optionsMenu);
    }

    /**
     * Set the frames per second for the animation. It is bounded by the speed of your hardware of course.
     */
    void  setFPS(int delay){
	boolean state = frozen;
	stopAnimation();
	delay = (delay > 0) ? (1000 / delay) : 100;
	timer.setDelay(delay);
	if( ! state )
	    startAnimation();
    }
    

    /**
     * Build the buttons for the applications 
     * If startCB is checked we are playing and the pause icon should show
     * If startCB is uncheck we are paused and the play icon should show
     * so cb==false->play
     *    cb==true->pause
     */
    void buildButtons(){

	playPic = getJarImages("pics/play.gif");
	pausePic = getJarImages("pics/pause.gif");
	pressPic = getJarImages("playing.gif");

	startCB = new JCheckBox(playPic,false);
	startCB.addActionListener(this);
	startCB.setSelectedIcon(pausePic);
	startCB.setPressedIcon(pressPic);
	startCB.setBorder(new EmptyBorder(0,0,0,0));
	startCB.setToolTipText("Play/Pause");
	startCB.setAlignmentX(Component.LEFT_ALIGNMENT);
	//startCB.setSelected(false);
    }

    private ImageIcon getJarImages(String imgName)
     {
       InputStream in = null;
       byte[] b = null;
       int size = 0;
    
       in = getClass().getResourceAsStream(imgName);
       try
       {
         size = in.available();
         b = new byte[size];
    
         in.read(b);
	 in.close();
       }
       catch (IOException e)
       {
         e.printStackTrace();
       }
       return new ImageIcon(Toolkit.getDefaultToolkit().createImage(b));
     } 
    
} // ENDS  CPUSchedulerFram




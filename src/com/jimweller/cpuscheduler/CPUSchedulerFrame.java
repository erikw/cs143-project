package com.jimweller.cpuscheduler;

//import Process;
//import CPUScheduler;
import java.util.Vector;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
//import ProcessPanel;
//import StatsPanel;
import javax.swing.filechooser.*;
//import BetterFileFilter;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.net.*;

//import org.reflections.scanners.SubTypesScanner;
import org.reflections.*;

/** 
 * CPUSchedulerFrame is a JFrame that contains and represents a CPUScheduler object. One can
 * load random and predetermined data sets, run simulations from a GUI and watch an animation
 * of the process thanks to to a time signal responded to using the ActionListener interface.
 */
public class CPUSchedulerFrame extends JFrame implements ActionListener {

    
    CPUScheduler cpu;

    //To store all available algorithms in
    Vector<SchedulingAlgorithm> algs;
    Vector<JRadioButtonMenuItem> algButtons;

    JCheckBox startCB;
    JTextField quantumField;
    ImageIcon playPic, pausePic, pressPic;


    JMenuBar menuBar;
    JMenu fileMenu, algorithmMenu, optionsMenu, speedMenu;
    JMenuItem newMI, openMI, resetMI, saveMI,quitMI;
    JRadioButtonMenuItem fps1MI,fps10MI,fps20MI,fps30MI,fps40MI,fps50MI,
	fps60MI,fps70MI,fps80MI,fps90MI,fps100MI;
    JCheckBoxMenuItem     preemptCB,priCB,showHiddenCB;


    JLabel statusBar,algolLbl;
    StatsPanel waitSP, turnSP,responseSP;
    ClockPanel cpuTimePanel;

    JSlider delaySlider, lengthSlider, countSlider, quantumSlider;

    JFileChooser openFileDialog;// = new JFileChooser(".");
    BetterFileFilter openFilter,saveFilter;// = new BetterFileFilter("dat");


    int frameNumber = -1;
    int fps = 30;
    Timer timer;
    boolean frozen = true;

    JPanel contentPane, queuePanel, buttonPanel;

    String fileName="";
	


    /**
     * Default constructor, builds and displays a random CPUScheduler object.
     */
    public CPUSchedulerFrame(){

	//setup animation and simulation
	cpu = new CPUScheduler();
	int delay = (fps > 0) ? (1000 / fps) : 100;
        timer = new Timer(delay,this);
        timer.setCoalesce(false); // don't combine queued events
	timer.setInitialDelay(0);
	
	// setup frame
	setTitle("CPU Scheduler Simulation");
	setSize(790,390);
	setIconImage(Toolkit.getDefaultToolkit().getImage("com/jimweller/cpuscheduler/pics/cpu.jpg"));
	
	getAlgorithms(); //must do first before buildMenus()
	buildButtons();
	queuePanel = new JPanel();
	buildMenus();
	buildStatusPanels();
	fillQueuePanel();
	buildFileDialog();
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
	bottomRow.setLayout(new FlowLayout(FlowLayout.CENTER));
	bottomRow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


	//topRow.add(statusBar,"North");
	topRow.add(queuePanel,"North");

	middleRow.add(cpuTimePanel);
	middleRow.add(responseSP);
	middleRow.add(turnSP);
	middleRow.add(waitSP);

	//bottomRow.add(middleRow);
	bottomRow.add(startCB);
	bottomRow.add(new JLabel("Quantum"));
	bottomRow.add(quantumField);

	masterPanel.add(topRow);
	masterPanel.add(middleRow);
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
		startCB.setSelected(false);
	    }
	    else{
		frozen = false;
		startAnimation();
		startCB.setSelected(true);
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
	/*else if( e.getSource() == priCB){
	    cpu.getAlgorithm().setPriority(priCB.getState());
	    }*/
	else if( e.getSource() == preemptCB){
	    try {
		OptionallyPreemptiveSchedulingAlgorithm sjfAlg = (OptionallyPreemptiveSchedulingAlgorithm)cpu.getAlgorithm();
		sjfAlg.setPreemptive(preemptCB.getState());
	    }
	    catch (Exception exc) {}
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
	else if( e.getSource() == openMI){
		//pause sim first
		frozen = true;
		stopAnimation(); 
		startCB.setSelected(false);
		
		//save current algorithm so we can give it to the new CPU object
		SchedulingAlgorithm alg = null;
		try {
			alg = cpu.getAlgorithm().getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
	    
	    if (alg != null)
	    	cpu.setAlgorithm(alg);
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

	else if( e.getSource() == resetMI){
		//pause simulator first
		frozen = true;
		stopAnimation(); 
		startCB.setSelected(false);
		
		//save current algorithm so we can give it to the new CPU object
		SchedulingAlgorithm alg = null;
		try {
			alg = cpu.getAlgorithm().getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    cpu.restore();
	    resetQueuePanel();
	    updateReadouts();
	    repaint();
	    if (alg != null)
	    	cpu.setAlgorithm(alg);
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

	else {
	    //Check if it's one of the algorithms
	    for (int i = 0; i < algButtons.size(); i++){
		if( e.getSource() == algButtons.get(i)){
		    SchedulingAlgorithm newAlg = algs.get(i);

		    //set quantum if RR alg
		    try {
			RoundRobinSchedulingAlgorithm RR = (RoundRobinSchedulingAlgorithm)newAlg;
			RR.setQuantum(Integer.parseInt(quantumField.getText()));
		    }
		    catch (Exception exc){}

		    //make preempt button work if SJF alg
		    try {
			OptionallyPreemptiveSchedulingAlgorithm sjfAlg = (OptionallyPreemptiveSchedulingAlgorithm)newAlg;
			preemptCB.setEnabled(true);
		    }
		    catch (Exception exc) {
			preemptCB.setEnabled(false);
		    }

		    cpu.setAlgorithm(newAlg);
		    algolLbl.setText(newAlg.getName());
		    break;
		}
	    }
	}

	//unrecognized
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
    void buildFileDialog(){
	openFileDialog = new JFileChooser(".");
	
	openFilter = new BetterFileFilter("");
	openFilter.addExtension("dat");
	openFilter.setDescription("Process Data");

	saveFilter = new BetterFileFilter("");
	saveFilter.addExtension("csv");
	saveFilter.setDescription("Comma Seperated Values");

    }


    
    /** Gory! Build all the menus for the application. */
    void buildMenus(){

	// main menu bar
	menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	


	// Build file menu
	fileMenu = new JMenu("File");

	newMI = new JMenuItem("New random source");
	newMI.addActionListener(this);
	fileMenu.add(newMI);

	openMI = new JMenuItem("Open Data Source...");
	openMI.addActionListener(this);
	fileMenu.add(openMI);

	saveMI = new JMenuItem("Save Statistics...");
	saveMI.addActionListener(this);
	fileMenu.add(saveMI);

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

	algButtons = new Vector<JRadioButtonMenuItem>();

	boolean setFirst = false;
	for (SchedulingAlgorithm alg : algs) {
	    JRadioButtonMenuItem newButton = new JRadioButtonMenuItem(alg.getName());
	    newButton.setToolTipText(alg.getName() + " scheduling");
	    newButton.addActionListener(this);
	    algogroup.add(newButton);
	    algorithmMenu.add(newButton);
	    algButtons.add(newButton);
	}

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
	priCB.setToolTipText("Use priority scheduling for algorithms supporting that feature.");
	priCB.setEnabled(true);
	priCB.addActionListener(this);
	//optionsMenu.add(priCB);

	// Preemptive menu option
	preemptCB = new JCheckBoxMenuItem("Preemption");
	preemptCB.setToolTipText("Preempt running process"+
				 " in algorithms supporting that feature.");
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
     */
    void buildButtons(){
	playPic  = new ImageIcon("src/pics/play.gif","play");
	pausePic  = new ImageIcon("src/pics/pause.gif","pause");
	pressPic  = new ImageIcon("src/pics/press.gif","press");

	startCB = new JCheckBox(playPic,false);
	startCB.addActionListener(this);
	startCB.setSelectedIcon(pausePic);
	startCB.setPressedIcon(pressPic);
	startCB.setBorder(new EmptyBorder(0,0,0,0));
	startCB.setToolTipText("Play/Pause");
	startCB.setAlignmentX(Component.LEFT_ALIGNMENT);

	quantumField = new JTextField("10", 10);
	quantumField.setToolTipText("Quantum");
	startCB.setAlignmentX(Component.RIGHT_ALIGNMENT);
    }

    /**
     * Find all available SchedulingAlgorithms and populate our lists with them.
     */
    void getAlgorithms(){
	algs = new Vector<SchedulingAlgorithm>();
		
	Reflections reflections = new Reflections("com.jimweller.cpuscheduler");    
	Set<Class<? extends SchedulingAlgorithm>> classes = reflections.getSubTypesOf(SchedulingAlgorithm.class);
	for (Class algClass : classes){
	    try{
		algs.add((SchedulingAlgorithm)algClass.newInstance());
	    } catch (InstantiationException e) {}
	    catch (IllegalAccessException e) {}
	}
    }

    
} // ENDS  CPUSchedulerFram




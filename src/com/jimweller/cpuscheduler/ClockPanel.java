package com.jimweller.cpuscheduler;

//import Process;
//import CPUScheduler;
import java.util.Vector;
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

/**
 * A simple panel for showing time/idle/burst for any quantifier.
 * Similar to a StatsPanel
 */
class ClockPanel extends JPanel{

    final static int width=170,height=80;


    JLabel timeLabel,idleLabel,busyLabel,timet,idle,busy;


    ClockPanel(String title){
        TitledBorder tBorder =  BorderFactory.createTitledBorder(title);
        setBorder( tBorder);
        setLayout(new GridLayout(0,2));

        timeLabel =  new JLabel("Time");
        timet = new JLabel(""+0);
        busyLabel  = new JLabel("Busy");
        busy =  new JLabel(""+0);
        idleLabel = new JLabel("Idle");
        idle  = new JLabel(""+0);

        add(timeLabel);
        add(timet);
        add(idleLabel);
        add(idle);
        add(busyLabel);
        add(busy);

        setSize(width,height);
        setMinimumSize(new Dimension(width,height));
    }

    /**
     * Update the displayed numbers
     */
    public void setStats(int t, int i, int b){
        timet.setText(Integer.toString(t));
        idle.setText(Integer.toString(i));
        busy.setText(Integer.toString(b));
    }


    public Dimension getMinimumSize(){
        return new Dimension(width,height);
    }

    public Dimension getPreferredSize(){
        return new Dimension(width,height);
    }

}


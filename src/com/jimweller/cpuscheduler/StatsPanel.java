package com.jimweller.cpuscheduler;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.*;

/**
 * A simple panel for showing min/mean/max for any quantifier.
 */
class StatsPanel extends JPanel{

    final static int width=170,height=80;

    JLabel minLabel,meanLabel,maxLabel,stdDevLabel, stdDev, min,mean,max;

    StatsPanel(){
    }

    StatsPanel(String title){
	TitledBorder tBorder =  BorderFactory.createTitledBorder(title);
	setBorder( tBorder);
	setLayout(new GridLayout(0,2));

	minLabel =  new JLabel("Min");
	min = new JLabel(""+0);
        meanLabel  = new JLabel("Mean");
	mean =  new JLabel(""+0);
	maxLabel = new JLabel("Max");
	max  = new JLabel(""+0);
	stdDevLabel = new JLabel("StdDev");
	stdDev  = new JLabel(""+0);

	add(minLabel);
	add(min);
	add(meanLabel);
	add(mean);
	add(maxLabel);
	add(max);
	add(stdDevLabel);
	add(stdDev);

	setSize(width,height);
	setMinimumSize(new Dimension(width,height));
    }

    
    /**
     * Update the statistic 
     */
    public void setStats(int mi, double me, int ma, double sd){
	NumberFormat nf = NumberFormat.getInstance();
	nf.setMaximumFractionDigits(2);
	nf.setMinimumFractionDigits(2);
	nf.setGroupingUsed(false);

	min.setText(Integer.toString(mi));
	max.setText(Integer.toString(ma));

	String s = nf.format(me);
	mean.setText(s);
	s = nf.format(sd);
	stdDev.setText(s);
    }


     public Dimension getMinimumSize(){
 	return new Dimension(width,height);
     }

     public Dimension getPreferredSize(){
 	return new Dimension(width,height);
     }

}

/** SJFSchedulingAlgorithm.java
 * 
 * A shortest job first scheduling algorithm.
 *
 * @author: Erik Westrup & Andrew Maltun
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class SJFSchedulingAlgorithm extends PrioritySchedulingAlgorithm {
    
    SJFSchedulingAlgorithm(){
    }
    
    @Override 
    protected void createQueue() {
    	pQ = new PriorityQueue<Process>(8, new SJFSComparator());
    }
    
    @Override
    public String getName(){
	return "Shortest job first";
    }

}

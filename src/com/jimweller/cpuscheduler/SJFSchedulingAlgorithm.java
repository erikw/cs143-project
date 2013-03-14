/** SJFSchedulingAlgorithm.java
 *
 * A shortest job first scheduling algorithm.
 *
 * @author: Erik Westrup & Andrew Maltun
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

/**
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 */

public class SJFSchedulingAlgorithm extends PrioritySchedulingAlgorithm {

    /**
     * Create a que with SJFSComparator.
     */
    @Override
    protected void createQueue() {
    	pQ = new PriorityQueue<Process>(8, new SJFSComparator());
    }

    @Override
    public String getName(){
		return "Shortest job first";
    }
}

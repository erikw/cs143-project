/**
 * 
 * A first-come first-served scheduling algorithm.
 *
 * @author: Erik Westrup & Andrew Maltun
 *
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {
    private PriorityQueue<Process> pque;

    FCFSSchedulingAlgorithm() {
		pque = new PriorityQueue<Process>(8, new ProcArrivalComparator());
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p) {
        //System.out.println("Adding jobb");
		pque.add(p);
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
		return pque.remove(p);
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	  when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        //System.out.println("Transfer in progress.");
    	Iterator<Process> iter = pque.iterator();
    	while (iter.hasNext()) {
			Process p = iter.next();
    		otherAlg.addJob(p);
			iter.remove();
    	}
    }


    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime) {
    	return pque.peek();
    }

    public boolean shouldPreempt(long currentTime) {
		return false; // TODO whatis this method?
    }


    public String getName() {
		return "First-come first-served";
    }
}

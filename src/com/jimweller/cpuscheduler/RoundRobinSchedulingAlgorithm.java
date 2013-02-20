/** RoundRobinSchedulingAlgorithm.java
 * 
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Erik Westrup & Andrew Maltun
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class RoundRobinSchedulingAlgorithm extends BaseSchedulingAlgorithm {

    /** the timeslice each process gets */
    private int quantum;
    private PriorityQueue<Process> pQ; 
    private Vector<Process> procs; 
    private int jobsAdded; 
    private int currentJob; 
    
    RoundRobinSchedulingAlgorithm() {
    	pQ = new PriorityQueue<Process>(8, new FCFSComparator());
    	procs = new Vector<Process>();
    	jobsAdded = 0;
    	currentJob = 0;
    }

    /** Add the new job to the correct queue. */
    public void addJob(Process p) {
    	
    	/** Need to use a prio queue here because we don't know what order jobs will be 
    	 * sent in. The prio queue sorts the jobs as they come in. We will process the prio 
    	 * queue later into something we can iterate through.     	
    	 */
    	pQ.add(p);
    	
    	/** jobsAdded variable helps later in processing prio queue into iterable */ 
    	jobsAdded++;
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
    	boolean result = false;
    	if (!pQ.isEmpty()) {
    		result = pQ.remove(p);
    		procs.remove(p);
    	}
    	return result;
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    	System.out.println("Transfer in progress...");
    	Iterator<Process> iter = pQ.iterator();
    	while (iter.hasNext()) {
			Process p = iter.next();
    		otherAlg.addJob(p);
    	}
    }

    /**
     * Get the value of quantum.
     * 
     * @return Value of quantum.
     */
    public int getQuantum() {
	return quantum;
    }

    /**
     * Set the value of quantum.
     * 
     * @param v
     *            Value to assign to quantum.
     */
    public void setQuantum(int v) {
	this.quantum = v;
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
    	
    	/** If any jobs have been added, need to strip them off the prio queue 
    	 * and put them at the end of the line for RR processing. */
    	while (jobsAdded > 0) {
    		procs.add(pQ.poll());
    		jobsAdded--; 
    	}
    	
    	/** If time quantum has run out, advance to the next job. 
    	 * Loop back to 0 once the currentJob counter reaches the end of the procs vector.  
    	 * */
    	if (currentTime % quantum == 0) {
    		currentJob = currentJob==procs.size() ? 0: currentJob + 1;
    	}
    	
    	return (procs.get(currentJob)); 
    	
    }

    public String getName() {
	return "Round Robin";
    }
}
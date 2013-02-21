/** PrioritySchedulingAlgorithm.java
 * 
 * A single-queue priority scheduling algorithm.
 *
 * @author: Erik Westrup & Andrew Maltun
 */
package com.jimweller.cpuscheduler;

import java.util.Iterator;
import java.util.PriorityQueue;

public class PrioritySchedulingAlgorithm extends BaseSchedulingAlgorithm implements OptionallyPreemptiveSchedulingAlgorithm {
	private boolean preemptive;

	PriorityQueue<Process> pQ; 

    PrioritySchedulingAlgorithm() {
    	activeJob = null;
    	preemptive = false;
    	createQueue();
    }

	/**
	 * Create a new prioQue.
	 */
    protected void createQueue() {
    	pQ = new PriorityQueue<Process>(8, new PriorityComparator());
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p) {
    	pQ.add(p);
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
    	return pQ.remove(p);
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	  when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        //System.out.println("Transfer in progress...");
    	Iterator<Process> iter = pQ.iterator();
    	while (iter.hasNext()) {
			Process p = iter.next();
    		otherAlg.addJob(p);
			iter.remove();
    	}
    }

    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime) {
    	if (isPreemptive()) {    	
    		activeJob = pQ.peek();
    	} else {
    		if (activeJob == null) {
    			activeJob = pQ.peek();
    		} else if (activeJob.isFinished()) {
    			activeJob = pQ.peek();
    		}
    	}
    	return activeJob;
    }

    public String getName() {
		return "Single-queue Priority";
    }

    /**
     * @return Value of preemptive.
     */
    public boolean isPreemptive() {
    	return preemptive;
    }

    /**
     * @param v  Value to assign to preemptive.
     */
    public void setPreemptive(boolean v) {
    	preemptive = v;
    }
}

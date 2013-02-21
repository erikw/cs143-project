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

    /** The timeslice each process gets */
    private int quantum;
    private static final int QUANTOM_DEFALT = 10;
    private PriorityQueue<Process> addedJobs; 
    //private ArrayList<Process> rrQ;  // TODO use Que of (Linked)List?
    private LinkedList<Process> rrQ;
    private int jobsAdded; 
    private int currentJob; 

    RoundRobinSchedulingAlgorithm() {
		addedJobs = new PriorityQueue<Process>(8, new ProcArrivalComparator()); // TODO use procArrivalcomparator?
    	rrQ = new LinkedList<Process>();
    	jobsAdded = 0;
    	currentJob = 0;
		quantum = QUANTOM_DEFALT;
    }

    /** Add the new job to the correct queue. */
    public void addJob(Process p) {

    	/** Need to use a prio queue here because we don't know what order jobs will be 
    	 * sent in. The prio queue sorts the jobs as they come in. We will process the prio 
    	 * queue later into something we can iterate through.     	
    	 */
    	addedJobs.add(p);

    	/** jobsAdded variable helps later in processing prio queue into iterable */ 
    	jobsAdded++;
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
    	boolean result = false;
    	result = addedJobs.remove(p);
    	result = rrQ.remove(p) || result;
    	return result;
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	  when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        //System.out.println("Transfer in progress...");
        handleNewJobs();
    	Iterator<Process> iter = addedJobs.iterator();
    	while (iter.hasNext()) {
    		otherAlg.addJob(iter.next());
    	}
    }

    /**
     * Get the value of quantum.
     * @return Value of quantum.
     */
    public int getQuantum() {
		return quantum;
    }

    /**
     * Set the value of quantum.
     * @param v Value to assign to quantum.
     */
    public void setQuantum(int v) {
		this.quantum = v;
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
		handleNewJobs();
        /** If time quantum has run out, advance to the next job. 
         * Loop back to 0 once the currentJob counter reaches the end of the rrQ vector.  
         * */
        //if (currentTime % quantum == 0) { // TODO what if the RR does not start at time=0?  Need to maintain state between calls.
            //currentJob = (currentJob == rrQ.size()) ? 0: currentJob + 1;
        //}



    	return rrQ.get(currentJob); 
    }

    public String getName() {
		return "Round Robin";
    }

    /** If any jobs have been added, need to strip them off the prio queue 
     * and put them at the end of the line for RR processing. */
    private void handleNewJobs() {
    	while (addedJobs.size() > 0) {
    		rrQ.add(addedJobs.poll());
    	}
    }
}

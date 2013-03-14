/** PrioritySchedulingAlgorithm.java
 *
 * A single-queue priority scheduling algorithm.
 *
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
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
     * Create a que with PriorityComparator..
     */
    protected void createQueue() {
    	pQ = new PriorityQueue<Process>(8, new PriorityComparator());
    }

    /**
     * Add the new job to the correct queue.
     * @param p Process to add.
     */
    public void addJob(Process p) {
    	pQ.add(p);
    }

    /**
     * Returns true if the job was present and was removed.
     * @param p Process to remove.
     * @return true if it was found and removed, false otherwise.
     */
    public boolean removeJob(Process p){
    	return pQ.remove(p);
    }

    /**
     * Transfer all the jobs in the queue of a SchedulingAlgorithm to another,
     * such as when switching to another algorithm in the GUI
     * @param otherAlg The other algorithm to transfer to.
	 */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    	Iterator<Process> iter = pQ.iterator();
    	while (iter.hasNext()) {
    		otherAlg.addJob(iter.next());
			iter.remove();
    	}
    }

    /**
     * Returns the next process that should be run by the CPU.
     * @param currentTime The current simulation time.
     * @return The next process or null.
     */
    public Process getNextJob(long currentTime) {
    	if (isPreemptive() || activeJob == null || activeJob.isFinished()) {
    			activeJob = pQ.peek();
    	}
    	return activeJob;
    }

    /**
     * Set preemption value.
     * @param v  Preemptive or not.
     */
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

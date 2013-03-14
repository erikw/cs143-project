/**
 *
 * A first-come first-served scheduling algorithm.
 *
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 *
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {

	/* The priority que. */
    private PriorityQueue<Process> pQ;

    FCFSSchedulingAlgorithm() {
		pQ = new PriorityQueue<Process>(8, new ProcArrivalComparator());
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
    public boolean removeJob(Process p) {
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
    	return pQ.peek();
    }

	/**
	 * Get the name of this algorithm.
	 * @return The name.
	 */
    public String getName() {
		return "First-come first-served";
    }
}

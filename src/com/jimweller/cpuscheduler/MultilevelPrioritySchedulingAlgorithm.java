/** MultilevelPrioritySchedulingAlgorithm.java
 *
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Erik Westrup & Andrew Maltun
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class MultilevelPrioritySchedulingAlgorithm extends RoundRobinSchedulingAlgorithm {

	private RoundRobinSchedulingAlgorithm que1;
	private RoundRobinSchedulingAlgorithm que2;
	private FCFSSchedulingAlgorithm	que3;

    /** The time slice each process gets */

    MultilevelPrioritySchedulingAlgorithm() {
    	que1 = new RoundRobinSchedulingAlgorithm();
    	que2 = new RoundRobinSchedulingAlgorithm();
    	que3 = new FCFSSchedulingAlgorithm();
    	setQuantum(10);
    }

    /** Add the new job to the correct queue. */
    public void addJob(Process p) {
    	SchedulingAlgorithm algo = getAlgoFromPrio(p);
    	algo.add(p);
    }

    private SchedulingAlgorithm getAlgoFromPrio(Process p) {
		SchedulingAlgorithm algo;
		int prio = p.getPriorityWeight();
    	if (between(prio, 0, 3)) {
			algo = que1;
    	} else if (between(prio, 4, 6)) {
			algo = que2;
    	} else {
			algo = que3;
    	}
    	return algo;
    }

    /**
     * Inclusively ranged range check.
     */
    private boolean between(int number, int min, int max) {
		return number >= min && number <= max;
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	  when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    }

    /**
     * Set the value of quantum.
     * @param v Value to assign to quantum.
     * NOTE this will only be changed in the GUI when RR is (re-)selected.
     */
    public void setQuantum(int v) {
    	super.setQuantum(v);
    	que1.setQuantum(v)
    	que2.setQuantum(2 * v);
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
    }

    public String getName() {
		return "Multilevel Priority";
    }
}


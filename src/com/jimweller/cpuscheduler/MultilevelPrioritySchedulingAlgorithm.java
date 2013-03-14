/** MultilevelPrioritySchedulingAlgorithm.java
 *
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class MultilevelPrioritySchedulingAlgorithm extends RoundRobinSchedulingAlgorithm implements OptionallyPreemptiveSchedulingAlgorithm {

	/* Are we preemptive? */
	private boolean preemptive;

	/* The current job running. */
	private Process curJob;

	/* Ques with lower number having higher prio. */
	private RoundRobinSchedulingAlgorithm que1;
	private RoundRobinSchedulingAlgorithm que2;
	private FCFSSchedulingAlgorithm	que3;


    /** The time slice each process gets */

    MultilevelPrioritySchedulingAlgorithm() {
    	que1 = new RoundRobinSchedulingAlgorithm();
    	que2 = new RoundRobinSchedulingAlgorithm();
    	que3 = new FCFSSchedulingAlgorithm();
    	setQuantum(10);
    	curJob = null;
    	preemptive = false;
    }

    /**
     * Add the new job to the correct queue.
     * @param p Process to add.
     */
    public void addJob(Process p) {
    	SchedulingAlgorithm algo = getAlgoFromPrio(p);
    	algo.addJob(p);
    }

    /**
     * Get an que to use based on the jobs priority.
     * @param p Job to check prio from.
     * @return An SchedulingAlgorithm to use for this que.
     */
    private SchedulingAlgorithm getAlgoFromPrio(Process p) {
		SchedulingAlgorithm algo;
		long prio = p.getPriorityWeight();
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
     * @param number Number to check.
     * @param min The lower bound.
     * @param max The higher bound.
     * @return True if number is in the range.
     */
    private boolean between(long number, int min, int max) {
		return number >= min && number <= max;
    }

    /**
     * Returns true if the job was present and was removed.
     * @param p Process to remove.
     * @return true if it was found and removed, false otherwise.
     */
    public boolean removeJob(Process p) {
    	return que1.removeJob(p) || que2.removeJob(p) || que3.removeJob(p);
    }

    /**
     * Transfer all the jobs in the queue of a SchedulingAlgorithm to another,
     * such as when switching to another algorithm in the GUI
     * @param otherAlg The other algorithm to transfer to.
	 */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    	que1.transferJobsTo(otherAlg);
    	que2.transferJobsTo(otherAlg);
    	que3.transferJobsTo(otherAlg);
    }

    /**
     * Set the value of quantum.
     * @param v Value to assign to quantum.
     * NOTE this will only be changed in the GUI when RR is (re-)selected.
     */
    public void setQuantum(int v) {
    	super.setQuantum(v);
    	que1.setQuantum(v);
    	que2.setQuantum(2 * v);
    }

    /**
     * Returns the next process that should be run by the CPU.
     * @param currentTime The current simulation time.
     * @return The next process or null.
     */
    public Process getNextJob(long currentTime) {
    	if (isPreemptive() || curJob == null || curJob.isFinished()) {
			curJob = pollJobFromQueues(currentTime);
    	}
    	return curJob;
    }

    /**
     * Fetch first job found from ques in priotity order.
     * @param currentTime The current simulation time.
     * @return A found process in prio order, or null.
     */
    private Process pollJobFromQueues(long currentTime) {
    	Process job = null;
    	if ((job = que1.getNextJob(currentTime)) != null) {
    		//System.out.println("Picked que1 job.");
    	} else if ((job = que2.getNextJob(currentTime)) != null) {
    		//System.out.println("Picked que2 job.");
		} else if ((job = que3.getNextJob(currentTime)) != null) {
			//System.out.println("Picked que3 job.");
		}
		return job;
    }

	/**
	 * Get the name of this algorithm.
	 * @return The name.
	 */
    public String getName() {
		return "Multilevel Priority";
    }

    /**
     * Are we preemeptive now?
     * @return True if we're preemptive now.
     */
    public boolean isPreemptive() {
    	return preemptive;
    }

    /**
     * Set preemption value.
     * @param v  Preemptive or not.
     */
    public void setPreemptive(boolean v) {
    	preemptive = v;
    }
}


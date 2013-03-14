/** RoundRobinSchedulingAlgorithm.java
 *
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class RoundRobinSchedulingAlgorithm extends BaseSchedulingAlgorithm {
    /** Default time quantum. */
    private static final int QUANTUM_DEFAULT = 10;

    /* The time slice each process gets */
    private int quantum;

    /* Newly added job not in rrQ yet. */
    private PriorityQueue<Process> addedJobs;

    /* RoundRobin que of jobs in the "robin". */
    private LinkedList<Process> rrQ;

    /* The currently running job. */
    private Process activeJob;

    /* Index of activeJob in rrQ. */
    private int currJob;

    /* How long the current job has been running. */
    private int curTimeQuantum;

    RoundRobinSchedulingAlgorithm() {
		addedJobs = new PriorityQueue<Process>(8, new ProcArrivalComparator());
    	rrQ = new LinkedList<Process>();
		quantum = QUANTUM_DEFAULT;
        curTimeQuantum = 0;
        currJob = 0;
    }

    /**
     * Add the new job to the correct queue.
     * @param p Process to add.
     */
    public void addJob(Process p) {
    	/* Need to use a prio queue here because we don't know what order jobs will be
    	 * sent in. The prio queue sorts the jobs as they come in. We will process the prio
    	 * queue later into something we can iterate through.
    	 */
    	addedJobs.add(p);
    }

    /**
     * Returns true if the job was present and was removed.
     * @param p Process to remove.
     * @return true if it was found and removed, false otherwise.
     */
    public boolean removeJob(Process p) {
    	return addedJobs.remove(p) || rrQ.remove(p);
    }

    /**
     * Transfer all the jobs in the queue of a SchedulingAlgorithm to another,
     * such as when switching to another algorithm in the GUI
     * @param otherAlg The other algorithm to transfer to.
	 */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        handleNewJobs();
    	Iterator<Process> iter = rrQ.iterator();
    	while (iter.hasNext()) {
    		otherAlg.addJob(iter.next());
			iter.remove();
    	}
    	activeJob = null;
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
     * NOTE this will only be changed in the GUI when RR is (re-)selected.
     */
    public void setQuantum(int v) {
		quantum = v;
    }

    /**
     * Returns the next process that should be run by the CPU.
     * @param currentTime The current simulation time.
     * @return The next process or null.
     */
    public Process getNextJob(long currentTime) {
		handleNewJobs();
		//printrrQ();
		if (quantum == 0) {
			// If the quantum is set to 0, don't try to do anything.
			activeJob = null;
		} else if (rrQ.size() > 0) {
			if (activeJob == null) {
				// At the beginning, activeJob==null. Grab job #0.
				activeJob = rrQ.get(currJob);
				curTimeQuantum = 0;
			} else if ((curTimeQuantum == quantum) && !activeJob.isFinished()) {
				/* If the quantum has completed, but the job is not finished,
				   leave it in the queue and set activeJob to the next job (can be circular). */
				currJob = (currJob + 1) % rrQ.size();
				activeJob = rrQ.get(currJob);
				curTimeQuantum = 0;
			} else if (activeJob.isFinished()) {
				// activeJob is not in rrQ since it has been cleaned up at the end of the last cycle.
				/* If the job finishes before the quantum runs out, leave currJob as is (jobs will shift down one index)
				 * and reset the quantum, UNLESS if the job that finished was the last job in the queue.
				 */
				if (currJob == rrQ.size()) {
					--currJob;
				}
				activeJob = rrQ.get(currJob);
				curTimeQuantum = 0;
			}
			++curTimeQuantum;
		} else {
			activeJob = null;
		}
		return activeJob;
    }

    /**
     * Set preemption value.
     * @param v  Preemptive or not.
     */
    public String getName() {
		return "Round Robin";
    }

    /**
     * If any jobs have been added, need to strip them off the prio queue
     * and put them at the end of the line for RR processing.
     */
    private void handleNewJobs() {
    	while (addedJobs.size() > 0) {
    		rrQ.offer(addedJobs.poll());
    	}
    }

    /**
     * Debug-print the current RRQ.
     */
    @SuppressWarnings("unused")
	private void printrrQ() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("rrQ = [");
    	for (Process p : rrQ) {
			sb.append(p.getPID()).append(", ");
    	}
    	sb.append("]\n");
    	System.out.println(sb);
    }
}

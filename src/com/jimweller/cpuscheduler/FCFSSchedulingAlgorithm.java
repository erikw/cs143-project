/** FCFSSchedulingAlgorithm.java
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

    FCFSSchedulingAlgorithm(){

    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p){

    }
    
    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
		return false; // TODO
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
	when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    }


    public boolean shouldPreempt(long currentTime){
		return false; // TODO
    }

    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime){
    	return null; // TODO
    }

    public String getName(){
	return "First-come first-served";
    }
}

package com.jimweller.cpuscheduler;

import java.util.Comparator;

/**
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 */

public class PriorityComparator implements Comparator<Process> {

	/**
	 * Comparsion between processes in priotiy.
	 */
	public int compare(Process p0, Process p1) {
		return (int) (p0.getPriorityWeight() - p1.getPriorityWeight());
	}
}

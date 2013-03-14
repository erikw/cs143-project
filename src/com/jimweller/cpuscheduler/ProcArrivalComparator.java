package com.jimweller.cpuscheduler;

import java.util.*;

/**
 * @author: Erik Westrup (50471668) & Andrew Maltun (82928815), group #27.
 */

public class ProcArrivalComparator implements Comparator<Process> {

	/**
	 * Compare processes in arrival time order.
	 */
	public int compare(Process p0, Process p1) {
		return (int) (p0.getArrivalTime() - p1.getArrivalTime());
	}
}

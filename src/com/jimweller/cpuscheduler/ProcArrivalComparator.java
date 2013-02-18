package com.jimweller.cpuscheduler;

import java.util.*;

public class ProcArrivalComparator implements Comparator<Process> {

	public int compare(Process p0, Process p1) {
		return (int) (p0.getArrivalTime() - p1.getArrivalTime());	
	}
	
	public boolean equals() {
		return true;
	}
}

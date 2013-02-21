package com.jimweller.cpuscheduler;

import java.util.Comparator;

public class SJFSComparator implements Comparator<Process> {

	public int compare(Process p0, Process p1) {
		return (int) (p0.getBurstTime() - p1.getBurstTime());
	}
}

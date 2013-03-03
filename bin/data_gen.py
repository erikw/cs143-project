#!/usr/bin/env python3
# Generate test data from a normal distribution.

import sys
import argparse
import inspect, os
import re
import random

__version__ = "0.1"

CPU_MIN = 1
CPU_MAX = 99
DELAY_MIN = 0
DELAY_MAX = 69
PRIO_MIN = 0
PRIO_MAX = 9

class Generator:
    def __init__(self, mean_cpu, mean_prio, number_procs):
        self.__mean_cpu = mean_cpu
        self.__mean_prio = mean_prio
        self.__number_procs = number_procs

    def __str__(self):
        return "Generator uses:\n Mean CPU\t\t=\t{:d}\n Mean priority\t\t=\t{:d}\n Number of processes\t=\t{:d}".format(self.__mean_cpu, self.__mean_prio, self.__number_procs)

    def generate(self):
        cpu_stddev = (CPU_MAX - CPU_MIN) / 6
        delay_stddev = (DELAY_MAX - DELAY_MIN) / 6
        prio_stddev = (PRIO_MAX - PRIO_MIN) / 6
        data = ""
        for i in range(0, self.__number_procs):
            cpu_time = self.gauss_nbr(self.__mean_cpu, cpu_stddev, CPU_MIN, CPU_MAX)
            prio = self.gauss_nbr(self.__mean_prio, prio_stddev, PRIO_MIN, PRIO_MAX)
            data = data + "{:d}\t0\t{:d}\n".format(cpu_time, prio)
        return data

    def gauss_nbr(self, mean, stddev, min, max):
        rnd = round(random.gauss(mean, stddev))
        if (rnd < min): rnd = min
        if (rnd > max): rnd = max
        return rnd


# Stolen from http://stackoverflow.com/questions/11272806/pythons-argparse-choices-constrained-printing 
class IntRange(object):
    def __init__(self, start, stop=None):
        if stop is None:
            start, stop = 0, start
        self.start, self.stop = start, stop

    def __call__(self, value):
        value = int(value)
        if value < self.start or value > self.stop:
            raise argparse.ArgumentTypeError("value outside of range [{:d}, {:d}]".format(self.start, self.stop))
        return value

def parse_args():
    parser = argparse.ArgumentParser(description="Generate test data to stdout (and parameter config output to stderr).")
    parser.add_argument("-v", "--version", action="store_true", help="Show version number.")
    parser.add_argument("-c", "--mean-cpu", type=IntRange(1, 99), default=49, help="The mean CPU burst time. Choose from [1, 99].")
    parser.add_argument("-p", "--mean-prio", type=IntRange(0, 9), default=4, help="The mean job priority burst time. Choose from [0, 9].")
    parser.add_argument("-n", "--number-procs", type=IntRange(0, sys.maxsize), default=50, help="How many precesses to generate. Choose from [0, {:d}]".format(sys.maxsize))
    args = parser.parse_args()
    if args.version:
        progname = re.sub("^.*\/", "", inspect.getfile(inspect.currentframe()))
        print("{:s} {:s}".format(progname, __version__))
        exit(0)
    return args.mean_cpu, args.mean_prio, args.number_procs

def main():
    mean_cpu, mean_prio, number_procs = parse_args()
    gen = Generator(mean_cpu, mean_prio, number_procs)
    sys.stderr.write(gen.__str__() + "\n")
    sys.stderr.write("Generating output...\n\n")
    print(gen.generate())
    exit(0)

if __name__ == '__main__':
    main()

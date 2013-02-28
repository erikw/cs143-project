#!/usr/bin/env python3
# Generate test data from a normal distribution.

import sys
import argparse
import inspect, os
import re

__version__ = "0.1"

class Generator:
    def __init__(self, mean_cpu, mean_prio, number_procs):
        self.__mean_cpu = mean_cpu
        self.__mean_prio = mean_prio
        self.__number_procs = number_procs

    def __str__(self):
        return "Generator uses:\n Mean CPU\t\t=\t{:d}\n Mean priotiy\t\t=\t{:d}\n Number of processes\t=\t{:d}".format(self.__mean_cpu, self.__mean_prio, self.__number_procs)

    def generate(self):
        pass


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
    parser = argparse.ArgumentParser(description="Generate test data to stdout.")
    parser.add_argument("-v", "--version", action="store_true", help="Show version number.")
    parser.add_argument("-c", "--mean-cpu", type=IntRange(1, 99), default=49, help="The mean CPU burst time. Choose from [1, 99].")
    parser.add_argument("-p", "--mean-prio", type=IntRange(0, 9), default=4, help="The mean job priority burst time. Choose from [0, 9].")
    parser.add_argument("-n", "--number-procs", type=IntRange(0, sys.maxsize), default=50, help="How many precesses to generate. Choose from [0, {:d}]".format(sys.maxsize))
    args = parser.parse_args()
    if args.version:
        progname = re.sub("^.*\/", "", inspect.getfile(inspect.currentframe()))
        print("{:s} {:s}".format(progname, __version__))
        exit()
    return args.mean_cpu, args.mean_prio, args.number_procs

def main():
    mean_cpu, mean_prio, number_procs = parse_args()
    gen = Generator(mean_cpu, mean_prio, number_procs)
    sys.stderr.write(gen.__str__() + "\n")
    sys.stderr.write("Generating output...\n\n")
    print(gen.generate())

if __name__ == '__main__':
    main()

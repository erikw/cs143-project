#!/usr/bin/env python3
# Generate test data from a normal distribution.

import argparse
import inspect, os
import re

VERSION=0.1

class Generator:
    A1_DEFAULT = 1
    A2_DEFAULT = 2
    A3_DEFAULT = 3

    def __init__(self, a1, a2, a3):
        self.__a1 = a1
        self.__a2 = a2
        self.__a3 = a3
        pass



#def parse_args(sysargv):
def parse_args():
    # short options
    # default values
    # help msgs
    # range value must be in interval [0,Inf[
    parser = argparse.ArgumentParser(description="Generate test data to stdout.")
    parser.add_argument("-v", "--version", action="store_true", help="Show version number.")
    args = parser.parse_args()
    if args.version:
        progname = re.sub("^.*\/", "", inspect.getfile(inspect.currentframe()))
        print("{:s} {:f}".format(progname, VERSION))
        exit()

def main():
    parse_args()
    #[a1, a2, a3 ] = parse_args()
    #gen = Generator(a1, a2, a3)
    #print(gen.generate())

if __name__ == '__main__':
    main()

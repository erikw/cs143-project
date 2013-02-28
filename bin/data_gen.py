#!/usr/bin/env python3
# Generate test data from a normal distribution.

import argparse

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
    parser = argparse.ArgumentParser()
    parser.parse_args()

def main():
    print("hai")
    parse_args()
    #[a1, a2, a3 ] = parse_args()
    #gen = Generator(a1, a2, a3)
    #print(gen.generate())

if __name__ == '__main__':
    main()

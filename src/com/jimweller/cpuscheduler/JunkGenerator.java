package com.jimweller.cpuscheduler;

import java.io.*;
import java.text.*;


/**
 * Quick and dirty main routine to generate *big* random data files for
 * testing purposes. 
 */
public class JunkGenerator {


    public static void main(String args[]){
        
        for(int i = 0; i < 200 ; i++){
	    long burst = (long) (Math.random() * 99 + 1 );
	    long delay = (long) (Math.random() * 50);
	    long priority = (long) (Math.random() * 9);
	    System.out.println(burst+"\t"+delay+"\t"+priority);
	}
    }



}

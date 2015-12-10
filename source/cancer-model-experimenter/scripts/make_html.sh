#!/bin/bash

# creates an HTML page from the logs of each replicate
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run

if [ ! -f ../cancer-outputs/$1.html ] ; then
   cat prefix.html postfix.html > ../cancer-outputs/$1.html ;
fi

echo "The following contains any errors encountered. " > /tmp/log_preface

if [ -f $1/*/slurm-*.out ] ; then
   cat /tmp/log_preface $1/*/slurm-*.out > ../cancer-outputs/$1.log ;
fi

rm /tmp/log_preface

if [ ! -f $1/*/16_runs.txt ] ; then
   exit 0 ;
fi

cat prefix.html $1/*/16_runs.txt postfix.html > ../cancer-outputs/$1.html

count=$(ls $1/*/16_runs.txt | wc -l)

#if last one then delete temporary files

if [ $count -eq $2 ] ; then 
   rm -R $1 ; 
fi






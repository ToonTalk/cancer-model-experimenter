#!/bin/bash

# creates an HTML page from the logs of each replicate
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run

pwd

echo $1

echo $2

cat prefix.html $1/*/16_runs.txt postfix.html > ../cancer-outputs/$1.html

cat $1/*/slurm-*.out > ../cancer-outputs/$1.log

count=$(ls $1/*/16_runs.txt | wc -l)

if [ $count -eq $2 ] ; then rm -R $1; fi

# should delete log*.html, nlogo, sh, etc files when the correct number of log files has been created
# or each batch job has finished




#!/bin/bash

# creates an HTML page from the logs of each replicate
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run

if [ ! -f ../cancer-outputs/$1.html ] ; then
   cat prefix.html postfix.html > ../cancer-outputs/$1.html
fi

log_count=$(ls $1/*/slurm-*.out | wc -l)

if [ $log_count -gt 0 ] ; then
  echo "The following contains errors encountered (if any). " > /tmp/log_preface
  cat /tmp/log_preface $1/*/slurm-*.out > ../cancer-outputs/$1.log
  rm /tmp/log_preface
else
  echo "Job is still in the ARC job queue. Try again later." > ../cancer-outputs/$1.log
fi

count=$(ls $1/*/16_runs.txt | wc -l)

if [ $count == 0 ] ; then
   # too early not results ready yet
   exit 0 
fi

cat prefix.html $1/*/16_runs.txt postfix.html > ../cancer-outputs/$1.html

#if last one then delete temporary files

if [ $count -eq $2 ] ; then 
   # delete the runs now so this only is called once
   rm $1/*/16_runs.txt
   # and schedule the full clean up since a script can't remove itself without errors
   cd temp
   sbatch --partition=devel clean_up_$1.sh $1
   cd ..
fi

#!/bin/bash

# $1 is the unique name of the experiment

# set the number of nodes to 1
#SBATCH --nodes=1

# set number of processes 1 per node
#SBATCH --ntasks-per-node=1

# set max wall time to 10 minutes (to qualify for developer priority)
#SBATCH --time=00:09:59

# set the name of the job
#SBATCH --job-name=cancer_model_completed

# remove all files from experiment folder

if [ $# -gt 0 ] ; then
   rm -R ~/cancer/$1
fi


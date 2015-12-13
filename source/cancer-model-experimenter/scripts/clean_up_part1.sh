#!/bin/bash

# $1 is the unique name of the experiment

# set the number of nodes to 1
#SBATCH --nodes=1

# set number of processes 1 per node
#SBATCH --ntasks-per-node=1

# set max wall time to 10 minutes
#SBATCH --time=00:09:59

# set the name of the job
#SBATCH --job-name=cancer_model_completed

# mail alerts when task finishes
#SBATCH --mail-type=END

# send mail to the following address


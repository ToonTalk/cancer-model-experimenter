#!/bin/bash

# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run

# set the number of nodes to 1
#SBATCH --nodes=1

# set number of processes 16 per node
#SBATCH --ntasks-per-node=16

# set max wall time to 12 hour
#SBATCH --time=12:00:00

# set the name of the job
#SBATCH --job-name=cancer_model

# mail alerts when task finishes
#SBATCH --mail-type=END

# send mail to the following address


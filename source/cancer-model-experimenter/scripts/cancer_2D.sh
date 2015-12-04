#!/bin/bash

# set the number of nodes to 8
#SBATCH --nodes=1

# set number of processes 16 per node
#SBATCH --ntasks-per-node=16

# set max wall time to 2 hour
#SBATCH --time=00:09:00

# set the name of the job
#SBATCH --job-name=cancer_model

# mail alerts at beginning and end 
#SBATCH --mail-type=BEGIN
#SBATCH --mail-type=END

# send mail to the following address
#SBATCH --mail-user=kenneth.kahn@it.ox.ac.uk

# start job from the directory it was submitted
cd $SLURM_SUBMIT_DIR

# load NetLogo
module load netlogo

# the model is cancer_2D.nlogo

netlogo-headless.sh \
--model $SLURM_SUBMIT_DIR/cancer_2D.nlogo \
--experiment multiple-runs \
--threads 16

cat log* > output.txt

rm log*.html


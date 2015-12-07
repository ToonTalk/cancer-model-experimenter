#!/bin/bash

# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run
# $3 should be either 2D or 3D

# set the number of nodes to 1
#SBATCH --nodes=1

# set number of processes 16 per node
#SBATCH --ntasks-per-node=16

# set max wall time to 2 hour
#SBATCH --time=00:09:00

# set the name of the job
#SBATCH --job-name=cancer_model

# start job from the directory it was submitted
cd $SLURM_SUBMIT_DIR

# load NetLogo
module load netlogo

netlogo-headless.sh \
--model $SLURM_SUBMIT_DIR/cancer_$3.nlogo \
--experiment multiple-runs \
--threads 16

cat log* > 16_runs.txt

rm log*

cd ../..

bash make_html.sh $1 $2
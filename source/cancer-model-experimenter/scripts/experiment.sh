#!/bin/bash

# each experiment needs its own directory for input files and modified NLOGO file
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run
# $3 which ARC queue - if dev uses development queue and 10 minute max -- anything else is production queue
# $4 email address for notifications (or none if none)
# $5 3d (lower case) or nothing (for 2D)

# create a clean_up script to run when finished 
# if an email address is provided then notify when clean up finished

if [ "$4" == none ] ; then
     cp clean_up.sh temp/clean_up_$1.sh
else
    echo "#SBATCH --mail-user=$4" > email_temp_file
    cat clean_up_part1.sh email_temp_file clean_up_part2.sh > temp/clean_up_$1.sh
    rm email_temp_file
fi

cd $1

INDEX=0
while [ $INDEX -lt $2 ] ; do
      mkdir batch_$INDEX
      cp ../run_cancer_model*.sh batch_$INDEX/
      cp ../cancer.nlogo*        batch_$INDEX/
      cp ../run_experiment.xml   batch_$INDEX/
      cp mutations.txt           batch_$INDEX/
      cp input.txt               batch_$INDEX/
      cp parameters.txt          batch_$INDEX/
      cp regulatoryGraph.html    batch_$INDEX/
      cd batch_$INDEX
      if [ "$3" == dev ] ; then
         sbatch --partition=devel run_cancer_model_dev.sh $1 $2 $5
      else
         sbatch run_cancer_model.sh $1 $2 $5
      fi
      cd ..
      let INDEX=INDEX+1
    done

cd ..







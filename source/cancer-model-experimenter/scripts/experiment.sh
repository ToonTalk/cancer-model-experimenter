#!/bin/bash

# each experiment needs its own directory for input files and modified NLOGO file
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run
# $3 which ARC queue - if dev uses development queue and 10 minute max -- anything else is production queue
# $4 email address for notifications (or none if none)
# $5 3d (lower case) or nothing (for 2D)

mkdir $1

cd $1

INDEX=0
while [ $INDEX -lt $2 ] ; do
      mkdir batch_$INDEX
      if [ "4" == none ] ; then
         cp ../run_cancer_model*.sh batch_$INDEX/
      else
         echo "#SBATCH --mail-user=$4" > email_temp_file
         cat ../run_cancer_model_part1.sh email_temp_file ../run_cancer_model_part2.sh > batch_$INDEX/run_cancer_model.sh
         rm email_temp_file
      fi
      cp ../cancer.nlogo*        batch_$INDEX/
      cp ../run_experiment.xml   batch_$INDEX/
      cp ../mutations.txt        batch_$INDEX/
      cp ../input.txt            batch_$INDEX/
      cp ../parameters.txt       batch_$INDEX/
      cp ../regulatoryGraph.html batch_$INDEX/
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

bash restore_from_master.sh





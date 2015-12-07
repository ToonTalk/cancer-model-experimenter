#!/bin/bash

# each experiment needs its own directory for input files and modified NLOGO file
# $1 is the unique name of the experiment
# $2 is the number of batches (16 replicates each) to run


mkdir $1

cd $1

INDEX=0
while [ $INDEX -lt $2 ]; do
      mkdir batch_$INDEX
      cp ../cancer_2D.sh         batch_$INDEX/
      cp ../cancer_2D.nlogo      batch_$INDEX/
      cp ../mutations.txt        batch_$INDEX/
      cp ../input.txt            batch_$INDEX/
      cp ../regulatoryGraph.html batch_$INDEX/
      cd batch_$INDEX
      sbatch --partition=devel cancer_2D.sh $1 $2
      cd ..
      let INDEX=INDEX+1
    done

cd ..

# should delete nlogo, sh, etc files




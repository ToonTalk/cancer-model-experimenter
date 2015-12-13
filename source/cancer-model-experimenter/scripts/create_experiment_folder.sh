#!/bin/bash

# add default files to experiment folder

# $1 is the experiment folder name

cd ~/cancer

mkdir $1

cp mutations.txt           $1
cp input.txt               $1
cp parameters.txt          $1
cp regulatoryGraph.html    $1

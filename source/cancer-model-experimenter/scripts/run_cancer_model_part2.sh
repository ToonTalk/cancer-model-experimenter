
# start job from the directory it was submitted
cd $SLURM_SUBMIT_DIR

# load NetLogo
module load netlogo

bash ../../netlogo-headless$3.sh \
--model $SLURM_SUBMIT_DIR/cancer.nlogo$3 \
--setup-file $SLURM_SUBMIT_DIR/run_experiment.xml \
--experiment run-experiment \
--threads 16

cat log* > 16_runs.txt

cd ../..

bash make_html.sh $1 $2
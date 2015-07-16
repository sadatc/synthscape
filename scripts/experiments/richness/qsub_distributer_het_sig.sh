#!/bin/bash


# broadcast
qsub -N het_broad_3s -v SIM_PARAMS="-include_entropy -interaction_levels 3 -model alife -interactions broadcast -species 'detector,extractor,transporter'" synthscape_job.sh

qsub -N het_broad_2s -v SIM_PARAMS="-include_entropy -interaction_levels 2 -model alife -interactions broadcast -species 'detector,extractor,transporter'" synthscape_job.sh

qsub -N het_broad_1s -v SIM_PARAMS="-include_entropy -interaction_levels 1  -model alife -interactions broadcast -species 'detector,extractor,transporter'" synthscape_job.sh


# unicast
qsub -N het_uni_3s -v SIM_PARAMS="-include_entropy -interaction_levels 3 -model alife -interactions unicast_n -species 'detector,extractor,transporter'" synthscape_job.sh

qsub -N het_uni_2s -v SIM_PARAMS="-include_entropy -interaction_levels 2 -interaction_levels a,b -model alife -interactions unicast_n -species 'detector,extractor,transporter'" synthscape_job.sh

qsub -N het_uni_1s -v SIM_PARAMS="-include_entropy -interaction_levels 1 -interaction_levels a -model alife -interactions unicast_n -species 'detector,extractor,transporter'" synthscape_job.sh



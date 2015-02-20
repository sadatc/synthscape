#!/bin/bash

# trail
qsub -N hom_trail_100 -v SIM_PARAMS="-interaction_quality highest -model alife -interactions trail -species homogenous" synthscape_job.sh

qsub -N hom_trail_75 -v SIM_PARAMS="-interaction_quality high -model alife -interactions trail -species homogenous" synthscape_job.sh

qsub -N hom_trail_50 -v SIM_PARAMS="-interaction_quality medium -model alife -interactions trail -species homogenous" synthscape_job.sh

qsub -N hom_trail_25 -v SIM_PARAMS="-interaction_quality poor -model alife -interactions trail -species homogenous" synthscape_job.sh

# broadcast
qsub -N hom_broad_100 -v SIM_PARAMS="-interaction_quality highest -model alife -interactions broadcast -species homogenous" synthscape_job.sh

qsub -N hom_broad_75 -v SIM_PARAMS="-interaction_quality high -model alife -interactions broadcast -species homogenous" synthscape_job.sh

qsub -N hom_broad_50 -v SIM_PARAMS="-interaction_quality medium -model alife -interactions broadcast -species homogenous" synthscape_job.sh

qsub -N hom_broad_25 -v SIM_PARAMS="-interaction_quality poor -model alife -interactions broadcast -species homogenous" synthscape_job.sh

# unicast
qsub -N hom_uni_100 -v SIM_PARAMS="-interaction_quality highest -model alife -interactions unicast_n -species homogenous" synthscape_job.sh

qsub -N hom_uni_75 -v SIM_PARAMS="-interaction_quality high -model alife -interactions unicast_n -species homogenous" synthscape_job.sh

qsub -N hom_uni_50 -v SIM_PARAMS="-interaction_quality medium -model alife -interactions unicast_n -species homogenous" synthscape_job.sh

qsub -N hom_uni_25 -v SIM_PARAMS="-interaction_quality poor -model alife -interactions unicast_n -species homogenous" synthscape_job.sh



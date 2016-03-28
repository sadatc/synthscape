#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# Last Updated: August, 2015
#
# Purpose: Runs the evenness experiments from the dissertation 
#
# A total of 3 experimental scenarios are run at once:
#          interactions (trail, broadcast, unicast) x
#
# Invoking: ./interactions.sh -server <andy|penzias> -batches <number> 
# -server: either the 'andy' or 'penzias' cluster (JVM is different)
# -experiments: the number of times each defined experiment is repeated
# -sims: number of simulations per experiments
#
# The following are optional. They are meant to more efficiently organize
# processes in the HPCC cluster. If none of the species (hom or het) is 
# given, experiments for ALL species are run. Similarly, if none of the 
# model (island, embodied, alife) is given, experiments for ALL models are run.
#

# Notes: (1) grabs all user parameters, (2) runs all the experiments in a loop
# (3) for each experiment uses the qsub script that actually invokes the 
# appropriate JVM with the simulation options


while [[ $# > 0 ]]
do
key="$1"

case $key in
    -server)
    SERVER="$2"
    shift 
    ;;
    -experiments)
    EXPERIMENTS="$2"
    shift 
    ;;
    -sims)
    SIMS="$2"
    shift 
    ;;
    *)
	    echo "program needs -server (andy|penzias) -experiments (1,2,...) -sims (1,2,3,...)"
    	exit 0
    ;;
esac
shift;
done

# check if server is present
if [ -z "${SERVER}" ]; then
    echo "server is missing: program needs -server (andy|penzias) -experiments (1,2,...) -sims (1,2,3,...)"
    exit 0
else 
	if [ ${SERVER} != "andy" -a ${SERVER} != "penzias" ] ; then
		echo "andy or penzias? program needs -server (andy|penzias) -experiments (1,2,...) -sims (1,2,3,...)"
	    exit 0
	fi
fi

# validate experiments option
if [ -z "${EXPERIMENTS}" ]; then
    echo "-experiments is missing: it's the num of simultaneous copies of each experiment"
    exit 0
else 
	re='^[0-9]+$'
	if ! [[ ${EXPERIMENTS} =~ $re ]] ; then
   		echo "-experiments: must be number" >&2; exit 1
	fi
fi

# validate sims option
if [ -z "${SIMS}" ]; then
    echo "-sims is missing: it's the number of sims run by each experiment "
    exit 0
else 
	re='^[0-9]+$'
	if ! [[ ${SIMS} =~ $re ]] ; then
   		echo "-sims: must be a number" >&2; exit 1
	fi
fi

# show params
echo "About to schedule on server: ${SERVER}"
echo "Number of simultaneous copies of each experiment: ${EXPERIMENTS}"
echo "Number of sims per experiment : ${SIMS}"



# loop over the experiments 
COUNTER=1
while [  $COUNTER -le ${EXPERIMENTS} ]; do

	# actual qsub jobs
	echo "scheduling batch : ${COUNTER}"
	
	echo "scheduling me_reg_24_r experiments"
	qsub -N me_reg_24_r -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -ranpr -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_diff_24_r experiments"
	qsub -N me_diff_24_r -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -ranpr -env_diff -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_vdiff_24_r experiments"
	qsub -N me_vdiff_24_r -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -ranpr -env_vdiff -repeat ${SIMS}" qsub_script.sh


	echo "scheduling me_reg_24_m111 experiments"
	qsub -N me_reg_24_m111 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '1:1:1' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m211 experiments"
	qsub -N me_reg_24_m211 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '2:1:1' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m121 experiments"
	qsub -N me_reg_24_m121 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '1:2:1' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m112 experiments"
	qsub -N me_reg_24_m112 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '1:1:2' -repeat ${SIMS}" qsub_script.sh


	echo "scheduling me_reg_24_m123 experiments"
	qsub -N me_reg_24_m123 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '1:2:3' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m132 experiments"
	qsub -N me_reg_24_m132 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '1:3:2' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m312 experiments"
	qsub -N me_reg_24_m312 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '3:1:2' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m321 experiments"
	qsub -N me_reg_24_m321 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '3:2:1' -repeat ${SIMS}" qsub_script.sh


	echo "scheduling me_reg_24_m213 experiments"
	qsub -N me_reg_24_m213 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '2:1:3' -repeat ${SIMS}" qsub_script.sh

	echo "scheduling me_reg_24_m231 experiments"
	qsub -N me_reg_24_m231 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -me -memp 24 -manpr '2:3:1' -repeat ${SIMS}" qsub_script.sh


	let COUNTER=COUNTER+1 
done


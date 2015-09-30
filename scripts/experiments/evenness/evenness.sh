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
	
	echo "scheduling evn_50_50_pln experiments"
	qsub -N evn_50_50_pln -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -de_msd -degofp 50 -degosc 50 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_50_5_pln experiments"
	qsub -N evn_50_5_pln -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -de_msd -degofp 50 -degosc 5 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_5_50_pln experiments"
	qsub -N evn_5_50_pln -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -de_msd -degofp 5 -degosc 50 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_5_5_pln experiments"
	qsub -N evn_5_5_pln -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -de_msd -degofp 5 -degosc 5 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_50_50_dff experiments"
	qsub -N evn_50_50_dff -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -env_diff -de_msd -degofp 50 -degosc 50 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_50_5_dff experiments"
	qsub -N evn_50_5_dff -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter'  -env_diff -de_msd -degofp 50 -degosc 5 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_5_50_dff experiments"
	qsub -N evn_5_50_dff -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -env_diff -de_msd -degofp 5 -degosc 50 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling evn_5_5_dff experiments"
	qsub -N evn_5_5_dff -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -env_diff -de_msd -degofp 5 -degosc 5 -repeat ${SIMS}" qsub_script.sh







	let COUNTER=COUNTER+1 
done


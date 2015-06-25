#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# Last Updated: June, 2015
#
# Purpose: Runs the interaction experiments from the dissertation and 
#          ECAL2015 conference
# A total of 24 experimental scenarios are run at once:
#          interaction quality (highest, high, medium, poor) x 
#          interactions (trail, broadcast, unicast) x
#          species (homogenous, heterogenous)
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
# -het: only run experiments with species = (extractor,transporter,detector)
# -hom: only run experiments with species = homogenous

# Notes: (1) grabs all user parameters, (2) runs all the experiments in a loop
# (3) for each experiment uses the qsub script that actually invokes the 
# appropriate JVM with the simulation options


SPECIES="ALL"
MODEL="ALL"


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
    -het)
    SPECIES="het"
    #shift 
    ;;
    -hom)
    SPECIES="hom"
    #shift 
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
    echo "server is missing: program needs -server (andy|penzias) -batches (1,2,...)"
    exit 0
else 
	if [ ${SERVER} != "andy" -a ${SERVER} != "penzias" ] ; then
		echo "andy or penzias? program needs -server (andy|penzias) -batches (1,2,...)"
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
echo "Species : ${SPECIES}"


# loop over the experiments 
COUNTER=1
while [  $COUNTER -le ${EXPERIMENTS} ]; do

	# actual qsub jobs
	echo "scheduling batch : ${COUNTER}"
	
	if [ ${MODEL} == "alife" -o ${MODEL} == "ALL" ] ; then
     echo "scheduling alife model experiments"
	 if [ ${SPECIES} == "hom" -o ${SPECIES} == "ALL" ] ; then 
	  echo "scheduling homogenous experiments"
	  qsub -N hom_trail_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions trail -species 'homogenous'" qsub_script.sh
	  qsub -N hom_trail_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions trail -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_trail_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions trail -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_trail_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions trail -species 'homogenous'" qsub_script.sh	  

	  qsub -N hom_broad_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions broadcast -species 'homogenous'" qsub_script.sh
	  qsub -N hom_broad_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions broadcast -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_broad_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions broadcast -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_broad_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions broadcast -species 'homogenous'" qsub_script.sh	  

	  qsub -N hom_uni_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions unicast_n -species 'homogenous'" qsub_script.sh
	  qsub -N hom_uni_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions unicast_n -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_uni_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions unicast_n -species 'homogenous'" qsub_script.sh	  
	  qsub -N hom_uni_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions unicast_n -species 'homogenous'" qsub_script.sh	  
	 fi
	 
	 if [ ${SPECIES} == "het" -o ${SPECIES} == "ALL" ] ; then 	
	  echo "scheduling heterogenous experiments"	 
	  qsub -N het_trail_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions trail -species 'detector,extractor,transporter'" qsub_script.sh
	  qsub -N het_trail_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions trail -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_trail_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions trail -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_trail_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions trail -species 'detector,extractor,transporter'" qsub_script.sh	  

	  qsub -N het_broad_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions broadcast -species 'detector,extractor,transporter'" qsub_script.sh
	  qsub -N het_broad_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions broadcast -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_broad_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions broadcast -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_broad_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions broadcast -species 'detector,extractor,transporter'" qsub_script.sh	  

	  qsub -N het_uni_100 -v SERVER=${SERVER},SIM_PARAMS="-iquality highest -model alife -interactions unicast_n -species 'detector,extractor,transporter'" qsub_script.sh
	  qsub -N het_uni_75 -v SERVER=${SERVER},SIM_PARAMS="-iquality high -model alife -interactions unicast_n -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_uni_50 -v SERVER=${SERVER},SIM_PARAMS="-iquality medium -model alife -interactions unicast_n -species 'detector,extractor,transporter'" qsub_script.sh	  
	  qsub -N het_uni_25 -v SERVER=${SERVER},SIM_PARAMS="-iquality low -model alife -interactions unicast_n -species 'detector,extractor,transporter'" qsub_script.sh	  
	 fi

	fi

	let COUNTER=COUNTER+1 
done


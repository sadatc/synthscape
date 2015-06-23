#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# Last Updated: June, 2015
#
# Purpose: Runs the general-trends experiments from the dissertation and 
#          BICT2014 conference
# A total of 24 experimental scenarios are run at once:
#          model (island, embodied, alife) x 
#          interactions (none, trail, broadcast, unicast) x
#          species (homogenous, heterogenous)
#
# Invoking: ./general_trends.sh --server <andy|penzias> --batches <number> 
# --server: either the andy or penzias cluster (JVM is different)
# --experiments: the number of times each defined experiment is repeated
# --sims: number of simulations per experiments
#
# Notes: (1) grabs all user parameters, (2) runs all the experiments in a loop
# (3) for each experiment uses the qsub script that actually invokes the 
# appropriate JVM with the simulation options


while [[ $# > 0 ]]
do
key="$1"

case $key in
    --server)
    SERVER="$2"
    shift 
    ;;
    --experiments)
    EXPERIMENTS="$2"
    shift 
    ;;
    --sims)
    SIMS="$2"
    shift 
    ;;

    *)
	    echo "program needs --server (andy|penzias) --experiments (1,2,...) --sims (1,2,3,...)"
    	exit 0
    ;;
esac
shift;
done

# check if server is present
if [ -z "${SERVER}" ]; then
    echo "server is missing: program needs --server (andy|penzias) --batches (1,2,...)"
    exit 0
else 
	if [ ${SERVER} != "andy" -a ${SERVER} != "penzias" ] ; then
		echo "andy or penzias? program needs --server (andy|penzias) --batches (1,2,...)"
	    exit 0
	fi
fi

# validate experiments option
if [ -z "${EXPERIMENTS}" ]; then
    echo "--experiments is missing: it's the num of simultaneous copies of each experiment"
    exit 0
else 
	re='^[0-9]+$'
	if ! [[ ${EXPERIMENTS} =~ $re ]] ; then
   		echo "--experiments: must be number" >&2; exit 1
	fi
fi

# validate sims option
if [ -z "${SIMS}" ]; then
    echo "--sims is missing: it's the number of sims run by each experiment "
    exit 0
else 
	re='^[0-9]+$'
	if ! [[ ${SIMS} =~ $re ]] ; then
   		echo "--sims: must be a number" >&2; exit 1
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

	qsub -N isl_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N isl_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N isl_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N isl_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh	


	qsub -N isl_het_none -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N isl_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N isl_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N isl_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh	



	qsub -N emb_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N emb_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N emb_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N emb_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh	


	qsub -N emb_het_none -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N emb_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N emb_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N emb_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh	



	qsub -N alife_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N alife_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N alife_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N alife_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'homogenous' -repeat ${SIMS}"	 qsub_script.sh	


	qsub -N alife_het_none -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh
	
	qsub -N alife_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N alife_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh

	qsub -N alife_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'detector,extractor,transporter' -repeat ${SIMS}"	 qsub_script.sh	


	
	let COUNTER=COUNTER+1 
	
done


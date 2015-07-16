#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# Last Updated: June, 2015
#
# Purpose: Runs the richness experiments from the dissertation 
#
# A total of 4x3 experimental scenarios are run at once:
#          interactions (trail, broadcast, unicast) x
#          species (mono,bi,tri,poly)
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
	echo "scheduling trail experiments"
	qsub -N rich_trail_mo -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail  -species '+d,+e,+t' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_trail_bi -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail  -species '^de,^dt,^et' -width 16 -height 16 -clones 4 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_trail_tri -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail  -species '*det' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_trail_po -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail  -species '+d,+e,+t,^de,^dt,^et,*det' -width 16 -height 16 -clones 2 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling broadcast experiments"
	qsub -N rich_broad_mo -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast  -species '+d,+e,+t' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_broad_bi -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast  -species '^de,^dt,^et' -width 16 -height 16 -clones 4 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_broad_tri -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast  -species '*det' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_broad_po -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast  -species '+d,+e,+t,^de,^dt,^et,*det' -width 16 -height 16 -clones 2 -repeat ${SIMS}" qsub_script.sh

	echo "scheduling unicast experiments"
	qsub -N rich_uni_mo -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n  -species '+d,+e,+t' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_uni_bi -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n  -species '^de,^dt,^et' -width 16 -height 16 -clones 4 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_uni_tri -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n  -species '*det' -width 16 -height 16 -clones 8 -repeat ${SIMS}" qsub_script.sh
	qsub -N rich_uni_po -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n  -species '+d,+e,+t,^de,^dt,^et,*det' -width 16 -height 16 -clones 2 -repeat ${SIMS}" qsub_script.sh




	let COUNTER=COUNTER+1 
done


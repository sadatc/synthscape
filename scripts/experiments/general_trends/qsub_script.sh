#!/bin/bash
# Author: Sadat Chowdhury (sadatc@gmail.com)
# Last Updated: June, 2015
#
# Purpose: Runs a PBS Job. 
# This sub-script is meant to be invoked by a parent script that selects
# appropriate simulation parameters and uses this script to distribute the 
# simulation on the PBS compute nodes.
# 

#PBS -q production

# node recoureces
#PBS -l select=1:ncpus=1

# place it on free slots
#PBS -l place=free

# get all execution env. vars into this one
#PBS -V

# join output and error
#PBS -j oe

# Change to working directory
cd ${PBS_O_WORKDIR}

# depending on the HPCC cluster server, pick the appropriate JVM
if [ ${SERVER} == "andy"  ] ; then
	# unfortunately, still runs the older JVM
	export PATH=/usr/lib64/jvm/jre-1.6.0-ibm/bin:${PATH}
else
	export PATH=/opt/jdk1.8.0_45/bin:${PATH}
fi	

export CLASSPATH=/home/s.chowdhury
DATA_DIR="${PBS_O_WORKDIR}/data/${PBS_JOBNAME}"

# create data directory based on job name
if [ ! -d "${DATA_DIR}" ]; then
   mkdir ${DATA_DIR}
fi

java -Xms2048M -Xmx2048M -jar  ../../../synthscape.jar ${SIM_PARAMS} -job_name ${PBS_JOBNAME} -ddir ${DATA_DIR} 

#!/bin/bash
#
# My serial PBS test job.
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

if [ ${SERVER} == "andy"  ] ; then
	export PATH=/usr/lib64/jvm/jre-1.6.0-ibm/bin/java:${PATH}
else
	export PATH=/opt/jdk1.8.0_45/bin:${PATH}
fi	

export CLASSPATH=/home/s.chowdhury
DATA_DIR="${PBS_O_WORKDIR}/data/${PBS_JOBNAME}"

# create data directory based on job name
if [ ! -d "${DATA_DIR}" ]; then
   mkdir ${DATA_DIR}
fi

java -Xms2048M -Xmx2048M -jar ../../../synthscape.jar -species ${SPECIES} -int ${INTERACTIONS} -model ${MODEL} -job_name ${PBS_JOBNAME} -ddir ${DATA_DIR} -repeat 500 
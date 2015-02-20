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

# job array with jobs numbered in range
#PBS -J 1-30

# join output and error
#PBS -j oe

# Change to working directory
cd ${PBS_O_WORKDIR}

export PATH=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin:${PATH}
export CLASSPATH=/home/s.chowdhury
DATA_DIR="${PBS_O_WORKDIR}/data/${PBS_JOBNAME}"

# create data directory based on job name
if [ ! -d "${DATA_DIR}" ]; then
   mkdir ${DATA_DIR}
fi

#java -Xms2048M -Xmx2048M -jar synthscape.jar -species hetero -interactions none -model island -job_name ${PBS_JOBNAME} -data_dir ${DATA_DIR} -repeat 50 -job_set ${PBS_ARRAY_INDEX}

java -Xms2048M -Xmx2048M -jar synthscape.jar -species ${SPECIES} -interactions ${INTERACTIONS} -model ${MODEL} -job_name ${PBS_JOBNAME} -data_dir ${DATA_DIR} -repeat 50 -job_set ${PBS_ARRAY_INDEX}
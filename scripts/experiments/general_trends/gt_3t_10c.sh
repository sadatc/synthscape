#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# {island,embodied,alife} x {none,trail,broadcast,unicast} x {homo, hetero}
# runs 24 scenarios for 3 task using "run blocks" of 5 experiments each

# number of repeats
SIMS=5 

# grab server param
while [[ $# > 0 ]]
do
key="$1"

case $key in
    -server)
    SERVER="$2"
    shift 
    ;;
    *)
	    echo "program needs -server (andy|penzias) "
    	exit 1
    ;;
esac
shift;
done


# island homo/hetero 
	 qsub -N i_ho_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_ho_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_ho_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_ho_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 

	 qsub -N i_he_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_he_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_he_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N i_he_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 

# embodied homo/hetero
	 qsub -N e_ho_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_ho_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_ho_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_ho_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 

	 qsub -N e_he_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_he_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_he_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N e_he_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 

# alife homo/hetero
	 qsub -N a_ho_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_ho_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_ho_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_ho_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'homogenous' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 


	 qsub -N a_he_n_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_he_t_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_he_b_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh
	 qsub -N a_he_u_3 -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'detector,extractor,transporter' -unconstrained -clones 10 -blocks ${SIMS}" qsub_script.sh	 

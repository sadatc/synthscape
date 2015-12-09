#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# {island,embodied,alife} x {none,trail,broadcast,unicast} x {homo, hetero}
# runs 24 scenarios for 3 task

# number of repeats
SIMS=30 

# island homo/hetero 
	  qsub -N isl_hom_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

	  qsub -N isl_het_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

# embodied homo/hetero
	  qsub -N emb_hom_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

	  qsub -N emb_het_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

# alife homo/hetero
	  qsub -N alife_hom_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  


	  qsub -N alife_het_non_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_tra_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_bro_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_uni_3ta -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'detector,extractor,transporter'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

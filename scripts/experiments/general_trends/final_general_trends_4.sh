#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# {island,embodied,alife} x {none,trail,broadcast,unicast} x {homo, hetero}
# runs 24 scenarios for 4 task

# number of repeats

SIMS=30 

# island homo/hetero 
	  qsub -N isl_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

	  qsub -N isl_het_none -v SERVER=${SERVER},SIM_PARAMS="-model island -int none -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model island -int trail -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model island -int broadcast -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N isl_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model island -int unicast_n -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

# embodied homo/hetero
	  qsub -N emb_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

	  qsub -N emb_het_none -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int none -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int trail -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int broadcast -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N emb_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model embodied -int unicast_n -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

# alife homo/hetero
	  qsub -N alife_hom_none -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_trail -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_broad -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_hom_uni -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'homogenous'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  


	  qsub -N alife_het_none -v SERVER=${SERVER},SIM_PARAMS="-model alife -int none -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_trail -v SERVER=${SERVER},SIM_PARAMS="-model alife -int trail -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_broad -v SERVER=${SERVER},SIM_PARAMS="-model alife -int broadcast -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh
	  qsub -N alife_het_uni -v SERVER=${SERVER},SIM_PARAMS="-model alife -int unicast_n -species 'detector,extractor,transporter,processor'  -unconstrained -repeat ${SIMS}" qsub_script.sh	  

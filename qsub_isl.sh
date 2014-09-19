#!/bin/bash

qsub -N isl_het_none -v MODEL=island,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N isl_het_trail -v MODEL=island,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N isl_het_broad -v MODEL=island,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N isl_het_uni -v MODEL=island,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N isl_hom_none -v MODEL=island,INTERACTIONS=none,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N isl_hom_trail -v MODEL=island,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N isl_hom_broad -v MODEL=island,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N isl_hom_uni -v MODEL=island,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_distribute_synthscape.sh


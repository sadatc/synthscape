#!/bin/bash

qsub -N emb_het_none -v MODEL=embodied,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N emb_het_trail -v MODEL=embodied,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N emb_het_broad -v MODEL=embodied,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N emb_het_uni -v MODEL=embodied,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N emb_hom_none -v MODEL=embodied,INTERACTIONS=none,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N emb_hom_trail -v MODEL=embodied,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N emb_hom_broad -v MODEL=embodied,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N emb_hom_uni -v MODEL=embodied,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_distribute_synthscape.sh


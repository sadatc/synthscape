#!/bin/bash

qsub -N alife_het_none -v MODEL=alife,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N alife_het_trail -v MODEL=alife,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N alife_het_broad -v MODEL=alife,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N alife_het_uni -v MODEL=alife,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_distribute_synthscape.sh

qsub -N alife_hom_none -v MODEL=alife,INTERACTIONS=none,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N alife_hom_trail -v MODEL=alife,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N alife_hom_broad -v MODEL=alife,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_distribute_synthscape.sh

qsub -N alife_hom_uni -v MODEL=alife,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_distribute_synthscape.sh


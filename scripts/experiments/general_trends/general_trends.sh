#!/bin/bash

while [[ $# > 0 ]]
do
key="$1"

case $key in
    --server)
    SERVER="$2"
    shift 
    ;;
    --batches)
    BATCHES="$2"
    shift 
    ;;
    *)
	    echo "program needs --server (andy|penzias) --batches (1,2,...)"
    	exit 0
    ;;
esac
shift;
done

# check if server is present
if [ -z "${SERVER}" ]; then
    echo "server is missing: program needs --server (andy|penzias) --batches (1,2,...)"
    exit 0
else 
	if [ ${SERVER} != "andy" -a ${SERVER} != "penzias" ] ; then
		echo "andy or penzias? program needs --server (andy|penzias) --batches (1,2,...)"
	    exit 0
	fi
fi

# check if nodes is present
if [ -z "${BATCHES}" ]; then
    echo "nodes is missing: program needs --server (andy|penzias) --batches (1,2,...)"
    exit 0
else 
	re='^[0-9]+$'
	if ! [[ ${BATCHES} =~ $re ]] ; then
   		echo "error: Not a number" >&2; exit 1
	fi
fi

echo "About to schedule on server: ${SERVER}"
echo "Number of batches per qsub: ${BATCHES}"

COUNTER=1

while [  $COUNTER -le ${BATCHES} ]; do

	# actual qsub jobs
	echo "scheduling batch : ${COUNTER}"

	qsub -N isl_het_none -v SERVER=${SERVER},MODEL=island,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N isl_het_trail -v SERVER=${SERVER},MODEL=island,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N isl_het_broad -v SERVER=${SERVER},MODEL=island,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N isl_het_uni -v SERVER=${SERVER},MODEL=island,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N isl_hom_none -v SERVER=${SERVER},MODEL=island,INTERACTIONS=none,SPECIES="'homogenous'" qsub_script.sh

	qsub -N isl_hom_trail -v SERVER=${SERVER},MODEL=island,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_script.sh

	qsub -N isl_hom_broad -v SERVER=${SERVER},MODEL=island,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_script.sh

	qsub -N isl_hom_uni -v SERVER=${SERVER},MODEL=island,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_script.sh

	qsub -N emb_het_none -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N emb_het_trail -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N emb_het_broad -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N emb_het_uni -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N emb_hom_none -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=none,SPECIES="'homogenous'" qsub_script.sh

	qsub -N emb_hom_trail -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_script.sh

	qsub -N emb_hom_broad -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_script.sh

	qsub -N emb_hom_uni -v SERVER=${SERVER},MODEL=embodied,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_script.sh

	qsub -N alife_het_none -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=none,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N alife_het_trail -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=trail,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N alife_het_broad -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=broadcast,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N alife_het_uni -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=unicast_n,SPECIES="'detector,extractor,transporter'" qsub_script.sh

	qsub -N alife_hom_none -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=none,SPECIES="'homogenous'" qsub_script.sh

	qsub -N alife_hom_trail -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=trail,SPECIES="'homogenous'" qsub_script.sh

	qsub -N alife_hom_broad -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=broadcast,SPECIES="'homogenous'" qsub_script.sh

	qsub -N alife_hom_uni -v SERVER=${SERVER},MODEL=alife,INTERACTIONS=unicast_n,SPECIES="'homogenous'" qsub_script.sh
	
	let COUNTER=COUNTER+1 
	
done


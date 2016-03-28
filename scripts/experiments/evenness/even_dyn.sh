#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# {island,embodied,alife} x {none,trail,broadcast,unicast} x {homo, hetero}
# runs 24 scenarios for 3 task using "run blocks" of 5 experiments each

# grab server param
while [[ $# > 0 ]]
do
key="$1"

case $key in
    -server)
    S="$2"
    shift 
    ;;
    *)
	    echo "program needs -server (andy|penzias) "
    	exit 1
    ;;
esac
shift;
done

qsub -N e4_tdp_3_24 -v S=${S},P="-experiment e4_tdp_3_24" qsub_script.sh
qsub -N e4_bdp_3_24 -v S=${S},P="-experiment e4_bdp_3_24" qsub_script.sh

qsub -N e4_tdf_3_24 -v S=${S},P="-experiment e4_tdf_3_24" qsub_script.sh
qsub -N e4_bdf_3_24 -v S=${S},P="-experiment e4_bdf_3_24" qsub_script.sh

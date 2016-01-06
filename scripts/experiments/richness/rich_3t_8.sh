#!/bin/bash

# Author: Sadat Chowdhury (sadatc@gmail.com)
# {island,embodied,alife} x {trail,broadcast,unicast} x {homo, hetero}
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

qsub -N e3_tmo_3_8 -v S=${S},P="-experiment e3_tmo_3_8" qsub_script.sh
qsub -N e3_tbi_3_8 -v S=${S},P="-experiment e3_tbi_3_8" qsub_script.sh
qsub -N e3_tpo_3_8 -v S=${S},P="-experiment e3_tpo_3_8" qsub_script.sh
qsub -N e3_ttr_3_8 -v S=${S},P="-experiment e3_ttr_3_8" qsub_script.sh
qsub -N e3_tho_3_8 -v S=${S},P="-experiment e3_tho_3_8" qsub_script.sh

qsub -N e3_bmo_3_8 -v S=${S},P="-experiment e3_bmo_3_8" qsub_script.sh
qsub -N e3_bbi_3_8 -v S=${S},P="-experiment e3_bbi_3_8" qsub_script.sh
qsub -N e3_bpo_3_8 -v S=${S},P="-experiment e3_bpo_3_8" qsub_script.sh
qsub -N e3_btr_3_8 -v S=${S},P="-experiment e3_btr_3_8" qsub_script.sh
qsub -N e3_bho_3_8 -v S=${S},P="-experiment e3_bho_3_8" qsub_script.sh

qsub -N e3_umo_3_8 -v S=${S},P="-experiment e3_umo_3_8" qsub_script.sh
qsub -N e3_ubi_3_8 -v S=${S},P="-experiment e3_ubi_3_8" qsub_script.sh
qsub -N e3_upo_3_8 -v S=${S},P="-experiment e3_upo_3_8" qsub_script.sh
qsub -N e3_utr_3_8 -v S=${S},P="-experiment e3_utr_3_8" qsub_script.sh
qsub -N e3_uho_3_8 -v S=${S},P="-experiment e3_uho_3_8" qsub_script.sh

	



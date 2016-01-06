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


qsub -N e3_tmo_4_48 -v S=${S},P="-experiment e3_tmo_4_48" qsub_script.sh
qsub -N e3_tbi_4_48 -v S=${S},P="-experiment e3_tbi_4_48" qsub_script.sh
qsub -N e3_ttr_4_48 -v S=${S},P="-experiment e3_ttr_4_48" qsub_script.sh
qsub -N e3_tpo_4_48 -v S=${S},P="-experiment e3_tpo_4_48" qsub_script.sh
qsub -N e3_tqu_4_48 -v S=${S},P="-experiment e3_tqu_4_48" qsub_script.sh
qsub -N e3_tho_4_48 -v S=${S},P="-experiment e3_tho_4_48" qsub_script.sh

qsub -N e3_bmo_4_48 -v S=${S},P="-experiment e3_bmo_4_48" qsub_script.sh
qsub -N e3_bbi_4_48 -v S=${S},P="-experiment e3_bbi_4_48" qsub_script.sh
qsub -N e3_btr_4_48 -v S=${S},P="-experiment e3_btr_4_48" qsub_script.sh
qsub -N e3_bpo_4_48 -v S=${S},P="-experiment e3_bpo_4_48" qsub_script.sh
qsub -N e3_bqu_4_48 -v S=${S},P="-experiment e3_bqu_4_48" qsub_script.sh
qsub -N e3_bho_4_48 -v S=${S},P="-experiment e3_bho_4_48" qsub_script.sh

qsub -N e3_umo_4_48 -v S=${S},P="-experiment e3_umo_4_48" qsub_script.sh
qsub -N e3_ubi_4_48 -v S=${S},P="-experiment e3_ubi_4_48" qsub_script.sh
qsub -N e3_utr_4_48 -v S=${S},P="-experiment e3_utr_4_48" qsub_script.sh
qsub -N e3_upo_4_48 -v S=${S},P="-experiment e3_upo_4_48" qsub_script.sh
qsub -N e3_uqu_4_48 -v S=${S},P="-experiment e3_uqu_4_48" qsub_script.sh
qsub -N e3_uho_4_48 -v S=${S},P="-experiment e3_uho_4_48" qsub_script.sh




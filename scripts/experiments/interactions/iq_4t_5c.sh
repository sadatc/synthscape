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


qsub -N e2_agts_4_5 -v S=${S},P="-experiment e2_agts_4_5" qsub_script.sh
qsub -N e2_agth_4_5 -v S=${S},P="-experiment e2_agth_4_5" qsub_script.sh
qsub -N e2_agtm_4_5 -v S=${S},P="-experiment e2_agtm_4_5" qsub_script.sh
qsub -N e2_agtl_4_5 -v S=${S},P="-experiment e2_agtl_4_5" qsub_script.sh

qsub -N e2_agbs_4_5 -v S=${S},P="-experiment e2_agbs_4_5" qsub_script.sh
qsub -N e2_agbh_4_5 -v S=${S},P="-experiment e2_agbh_4_5" qsub_script.sh
qsub -N e2_agbm_4_5 -v S=${S},P="-experiment e2_agbm_4_5" qsub_script.sh
qsub -N e2_agbl_4_5 -v S=${S},P="-experiment e2_agbl_4_5" qsub_script.sh

qsub -N e2_agus_4_5 -v S=${S},P="-experiment e2_agus_4_5" qsub_script.sh
qsub -N e2_aguh_4_5 -v S=${S},P="-experiment e2_aguh_4_5" qsub_script.sh
qsub -N e2_agum_4_5 -v S=${S},P="-experiment e2_agum_4_5" qsub_script.sh
qsub -N e2_agul_4_5 -v S=${S},P="-experiment e2_agul_4_5" qsub_script.sh

qsub -N e2_asts_4_5 -v S=${S},P="-experiment e2_asts_4_5" qsub_script.sh
qsub -N e2_asth_4_5 -v S=${S},P="-experiment e2_asth_4_5" qsub_script.sh
qsub -N e2_astm_4_5 -v S=${S},P="-experiment e2_astm_4_5" qsub_script.sh
qsub -N e2_astl_4_5 -v S=${S},P="-experiment e2_astl_4_5" qsub_script.sh

qsub -N e2_asbs_4_5 -v S=${S},P="-experiment e2_asbs_4_5" qsub_script.sh
qsub -N e2_asbh_4_5 -v S=${S},P="-experiment e2_asbh_4_5" qsub_script.sh
qsub -N e2_asbm_4_5 -v S=${S},P="-experiment e2_asbm_4_5" qsub_script.sh
qsub -N e2_asbl_4_5 -v S=${S},P="-experiment e2_asbl_4_5" qsub_script.sh

qsub -N e2_asus_4_5 -v S=${S},P="-experiment e2_asus_4_5" qsub_script.sh
qsub -N e2_asuh_4_5 -v S=${S},P="-experiment e2_asuh_4_5" qsub_script.sh
qsub -N e2_asum_4_5 -v S=${S},P="-experiment e2_asum_4_5" qsub_script.sh
qsub -N e2_asul_4_5 -v S=${S},P="-experiment e2_asul_4_5" qsub_script.sh



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



qsub -N e2_agts_3_5 -v S=${S},P="-experiment e2_agts_3_5" qsub_script.sh
qsub -N e2_agth_3_5 -v S=${S},P="-experiment e2_agth_3_5" qsub_script.sh
qsub -N e2_agtm_3_5 -v S=${S},P="-experiment e2_agtm_3_5" qsub_script.sh
qsub -N e2_agtl_3_5 -v S=${S},P="-experiment e2_agtl_3_5" qsub_script.sh

qsub -N e2_agbs_3_5 -v S=${S},P="-experiment e2_agbs_3_5" qsub_script.sh
qsub -N e2_agbh_3_5 -v S=${S},P="-experiment e2_agbh_3_5" qsub_script.sh
qsub -N e2_agbm_3_5 -v S=${S},P="-experiment e2_agbm_3_5" qsub_script.sh
qsub -N e2_agbl_3_5 -v S=${S},P="-experiment e2_agbl_3_5" qsub_script.sh

qsub -N e2_agus_3_5 -v S=${S},P="-experiment e2_agus_3_5" qsub_script.sh
qsub -N e2_aguh_3_5 -v S=${S},P="-experiment e2_aguh_3_5" qsub_script.sh
qsub -N e2_agum_3_5 -v S=${S},P="-experiment e2_agum_3_5" qsub_script.sh
qsub -N e2_agul_3_5 -v S=${S},P="-experiment e2_agul_3_5" qsub_script.sh

qsub -N e2_asts_3_5 -v S=${S},P="-experiment e2_asts_3_5" qsub_script.sh
qsub -N e2_asth_3_5 -v S=${S},P="-experiment e2_asth_3_5" qsub_script.sh
qsub -N e2_astm_3_5 -v S=${S},P="-experiment e2_astm_3_5" qsub_script.sh
qsub -N e2_astl_3_5 -v S=${S},P="-experiment e2_astl_3_5" qsub_script.sh

qsub -N e2_asbs_3_5 -v S=${S},P="-experiment e2_asbs_3_5" qsub_script.sh
qsub -N e2_asbh_3_5 -v S=${S},P="-experiment e2_asbh_3_5" qsub_script.sh
qsub -N e2_asbm_3_5 -v S=${S},P="-experiment e2_asbm_3_5" qsub_script.sh
qsub -N e2_asbl_3_5 -v S=${S},P="-experiment e2_asbl_3_5" qsub_script.sh

qsub -N e2_asus_3_5 -v S=${S},P="-experiment e2_asus_3_5" qsub_script.sh
qsub -N e2_asuh_3_5 -v S=${S},P="-experiment e2_asuh_3_5" qsub_script.sh
qsub -N e2_asum_3_5 -v S=${S},P="-experiment e2_asum_3_5" qsub_script.sh
qsub -N e2_asul_3_5 -v S=${S},P="-experiment e2_asul_3_5" qsub_script.sh

	



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


# island homo/hetero 
	 qsub -N e1_ign_3_10 -v S=${S},P="-experiment e1_ign_3_10" qsub_script.sh
	 qsub -N e1_igt_3_10 -v S=${S},P="-experiment e1_igt_3_10" qsub_script.sh
	 qsub -N e1_igb_3_10 -v S=${S},P="-experiment e1_igb_3_10" qsub_script.sh
	 qsub -N e1_igu_3_10 -v S=${S},P="-experiment e1_igu_3_10" qsub_script.sh	 

	 qsub -N e1_isn_3_10 -v S=${S},P="-experiment e1_isn_3_10" qsub_script.sh
	 qsub -N e1_ist_3_10 -v S=${S},P="-experiment e1_ist_3_10" qsub_script.sh
	 qsub -N e1_isb_3_10 -v S=${S},P="-experiment e1_isb_3_10" qsub_script.sh
	 qsub -N e1_isu_3_10 -v S=${S},P="-experiment e1_isu_3_10" qsub_script.sh	

# embodied homo/hetero

	 qsub -N e1_egn_3_10 -v S=${S},P="-experiment e1_egn_3_10" qsub_script.sh
	 qsub -N e1_egt_3_10 -v S=${S},P="-experiment e1_egt_3_10" qsub_script.sh
	 qsub -N e1_egb_3_10 -v S=${S},P="-experiment e1_egb_3_10" qsub_script.sh
	 qsub -N e1_egu_3_10 -v S=${S},P="-experiment e1_egu_3_10" qsub_script.sh	 

	 qsub -N e1_esn_3_10 -v S=${S},P="-experiment e1_esn_3_10" qsub_script.sh
	 qsub -N e1_est_3_10 -v S=${S},P="-experiment e1_est_3_10" qsub_script.sh
	 qsub -N e1_esb_3_10 -v S=${S},P="-experiment e1_esb_3_10" qsub_script.sh
	 qsub -N e1_esu_3_10 -v S=${S},P="-experiment e1_esu_3_10" qsub_script.sh	


# alife homo/hetero

	 qsub -N e1_agn_3_10 -v S=${S},P="-experiment e1_agn_3_10" qsub_script.sh
	 qsub -N e1_agt_3_10 -v S=${S},P="-experiment e1_agt_3_10" qsub_script.sh
	 qsub -N e1_agb_3_10 -v S=${S},P="-experiment e1_agb_3_10" qsub_script.sh
	 qsub -N e1_agu_3_10 -v S=${S},P="-experiment e1_agu_3_10" qsub_script.sh	 

	 qsub -N e1_asn_3_10 -v S=${S},P="-experiment e1_asn_3_10" qsub_script.sh
	 qsub -N e1_ast_3_10 -v S=${S},P="-experiment e1_ast_3_10" qsub_script.sh
	 qsub -N e1_asb_3_10 -v S=${S},P="-experiment e1_asb_3_10" qsub_script.sh
	 qsub -N e1_asu_3_10 -v S=${S},P="-experiment e1_asu_3_10" qsub_script.sh	



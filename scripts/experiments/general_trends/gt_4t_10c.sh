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
	 qsub -N e1_ign_4_10 -v S=${S},P="-experiment e1_ign_4_10" qsub_script.sh
	 qsub -N e1_igt_4_10 -v S=${S},P="-experiment e1_igt_4_10" qsub_script.sh
	 qsub -N e1_igb_4_10 -v S=${S},P="-experiment e1_igb_4_10" qsub_script.sh
	 qsub -N e1_igu_4_10 -v S=${S},P="-experiment e1_igu_4_10" qsub_script.sh	 

	 qsub -N e1_isn_4_10 -v S=${S},P="-experiment e1_isn_4_10" qsub_script.sh
	 qsub -N e1_ist_4_10 -v S=${S},P="-experiment e1_ist_4_10" qsub_script.sh
	 qsub -N e1_isb_4_10 -v S=${S},P="-experiment e1_isb_4_10" qsub_script.sh
	 qsub -N e1_isu_4_10 -v S=${S},P="-experiment e1_isu_4_10" qsub_script.sh	

# embodied homo/hetero

	 qsub -N e1_egn_4_10 -v S=${S},P="-experiment e1_egn_4_10" qsub_script.sh
	 qsub -N e1_egt_4_10 -v S=${S},P="-experiment e1_egt_4_10" qsub_script.sh
	 qsub -N e1_egb_4_10 -v S=${S},P="-experiment e1_egb_4_10" qsub_script.sh
	 qsub -N e1_egu_4_10 -v S=${S},P="-experiment e1_egu_4_10" qsub_script.sh	 

	 qsub -N e1_esn_4_10 -v S=${S},P="-experiment e1_esn_4_10" qsub_script.sh
	 qsub -N e1_est_4_10 -v S=${S},P="-experiment e1_est_4_10" qsub_script.sh
	 qsub -N e1_esb_4_10 -v S=${S},P="-experiment e1_esb_4_10" qsub_script.sh
	 qsub -N e1_esu_4_10 -v S=${S},P="-experiment e1_esu_4_10" qsub_script.sh	


# alife homo/hetero

	 qsub -N e1_agn_4_10 -v S=${S},P="-experiment e1_agn_4_10" qsub_script.sh
	 qsub -N e1_agt_4_10 -v S=${S},P="-experiment e1_agt_4_10" qsub_script.sh
	 qsub -N e1_agb_4_10 -v S=${S},P="-experiment e1_agb_4_10" qsub_script.sh
	 qsub -N e1_agu_4_10 -v S=${S},P="-experiment e1_agu_4_10" qsub_script.sh	 

	 qsub -N e1_asn_4_10 -v S=${S},P="-experiment e1_asn_4_10" qsub_script.sh
	 qsub -N e1_ast_4_10 -v S=${S},P="-experiment e1_ast_4_10" qsub_script.sh
	 qsub -N e1_asb_4_10 -v S=${S},P="-experiment e1_asb_4_10" qsub_script.sh
	 qsub -N e1_asu_4_10 -v S=${S},P="-experiment e1_asu_4_10" qsub_script.sh	



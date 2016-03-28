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

qsub -N e4_tmf_111_3_24 -v S=${S},P="-experiment e4_tmf_111_3_24" qsub_script.sh
qsub -N e4_tmf_211_3_24 -v S=${S},P="-experiment e4_tmf_211_3_24" qsub_script.sh
qsub -N e4_tmf_121_3_24 -v S=${S},P="-experiment e4_tmf_121_3_24" qsub_script.sh
qsub -N e4_tmf_112_3_24 -v S=${S},P="-experiment e4_tmf_112_3_24" qsub_script.sh
qsub -N e4_tmf_123_3_24 -v S=${S},P="-experiment e4_tmf_123_3_24" qsub_script.sh
qsub -N e4_tmf_132_3_24 -v S=${S},P="-experiment e4_tmf_132_3_24" qsub_script.sh
qsub -N e4_tmf_312_3_24 -v S=${S},P="-experiment e4_tmf_312_3_24" qsub_script.sh
qsub -N e4_tmf_321_3_24 -v S=${S},P="-experiment e4_tmf_321_3_24" qsub_script.sh
qsub -N e4_tmf_213_3_24 -v S=${S},P="-experiment e4_tmf_213_3_24" qsub_script.sh
qsub -N e4_tmf_231_3_24 -v S=${S},P="-experiment e4_tmf_231_3_24" qsub_script.sh

qsub -N e4_bmf_111_3_24 -v S=${S},P="-experiment e4_bmf_111_3_24" qsub_script.sh
qsub -N e4_bmf_211_3_24 -v S=${S},P="-experiment e4_bmf_211_3_24" qsub_script.sh
qsub -N e4_bmf_121_3_24 -v S=${S},P="-experiment e4_bmf_121_3_24" qsub_script.sh
qsub -N e4_bmf_112_3_24 -v S=${S},P="-experiment e4_bmf_112_3_24" qsub_script.sh
qsub -N e4_bmf_123_3_24 -v S=${S},P="-experiment e4_bmf_123_3_24" qsub_script.sh
qsub -N e4_bmf_132_3_24 -v S=${S},P="-experiment e4_bmf_132_3_24" qsub_script.sh
qsub -N e4_bmf_312_3_24 -v S=${S},P="-experiment e4_bmf_312_3_24" qsub_script.sh
qsub -N e4_bmf_321_3_24 -v S=${S},P="-experiment e4_bmf_321_3_24" qsub_script.sh
qsub -N e4_bmf_213_3_24 -v S=${S},P="-experiment e4_bmf_213_3_24" qsub_script.sh
qsub -N e4_bmf_231_3_24 -v S=${S},P="-experiment e4_bmf_231_3_24" qsub_script.sh
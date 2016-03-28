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

qsub -N e4_tmp_111_3_24 -v S=${S},P="-experiment e4_tmp_111_3_24" qsub_script.sh

qsub -N e4_tmp_211_3_24 -v S=${S},P="-experiment e4_tmp_211_3_24" qsub_script.sh
qsub -N e4_tmp_121_3_24 -v S=${S},P="-experiment e4_tmp_121_3_24" qsub_script.sh
qsub -N e4_tmp_112_3_24 -v S=${S},P="-experiment e4_tmp_112_3_24" qsub_script.sh

qsub -N e4_tmp_123_3_24 -v S=${S},P="-experiment e4_tmp_123_3_24" qsub_script.sh
qsub -N e4_tmp_132_3_24 -v S=${S},P="-experiment e4_tmp_132_3_24" qsub_script.sh

qsub -N e4_tmp_312_3_24 -v S=${S},P="-experiment e4_tmp_312_3_24" qsub_script.sh
qsub -N e4_tmp_321_3_24 -v S=${S},P="-experiment e4_tmp_321_3_24" qsub_script.sh

qsub -N e4_tmp_213_3_24 -v S=${S},P="-experiment e4_tmp_213_3_24" qsub_script.sh
qsub -N e4_tmp_231_3_24 -v S=${S},P="-experiment e4_tmp_231_3_24" qsub_script.sh




qsub -N e4_bmp_111_3_24 -v S=${S},P="-experiment e4_bmp_111_3_24" qsub_script.sh

qsub -N e4_bmp_211_3_24 -v S=${S},P="-experiment e4_bmp_211_3_24" qsub_script.sh
qsub -N e4_bmp_121_3_24 -v S=${S},P="-experiment e4_bmp_121_3_24" qsub_script.sh
qsub -N e4_bmp_112_3_24 -v S=${S},P="-experiment e4_bmp_112_3_24" qsub_script.sh

qsub -N e4_bmp_123_3_24 -v S=${S},P="-experiment e4_bmp_123_3_24" qsub_script.sh
qsub -N e4_bmp_132_3_24 -v S=${S},P="-experiment e4_bmp_132_3_24" qsub_script.sh

qsub -N e4_bmp_312_3_24 -v S=${S},P="-experiment e4_bmp_312_3_24" qsub_script.sh
qsub -N e4_bmp_321_3_24 -v S=${S},P="-experiment e4_bmp_321_3_24" qsub_script.sh

qsub -N e4_bmp_213_3_24 -v S=${S},P="-experiment e4_bmp_213_3_24" qsub_script.sh
qsub -N e4_bmp_231_3_24 -v S=${S},P="-experiment e4_bmp_231_3_24" qsub_script.sh
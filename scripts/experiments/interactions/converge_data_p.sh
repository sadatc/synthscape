#!/bin/sh

SYNTH_BIN_DIR=/Users/sadat/synthscape/bin
DATA_DIR=/Users/sadat/penzias_data
OUTPUT_DIR=/Users/sadat/penzias_data/convergence

# create OUTPUT_DIR if it doesn't exist
if [ ! -d "${OUTPUT_DIR}" ]; then
   mkdir ${OUTPUT_DIR}
fi

# clear everything that exists in the OUTPUT_DIR
rm -rf ${OUTPUT_DIR}/*

cd ${SYNTH_BIN_DIR}

# ISLAND MODEL

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_het_none/ -outfile ${OUTPUT_DIR}/ihen.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_het_broad/ -outfile ${OUTPUT_DIR}/iheb.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_het_trail/ -outfile ${OUTPUT_DIR}/ihet.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_het_uni/ -outfile ${OUTPUT_DIR}/iheu.csv


java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_hom_none/ -outfile ${OUTPUT_DIR}/ihon.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_hom_broad/ -outfile ${OUTPUT_DIR}/ihob.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_hom_trail/ -outfile ${OUTPUT_DIR}/ihot.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/isl_hom_uni/ -outfile ${OUTPUT_DIR}/ihou.csv

# EMBODIED MODEL

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_het_none/ -outfile ${OUTPUT_DIR}/ehen.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_het_broad/ -outfile ${OUTPUT_DIR}/eheb.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_het_trail/ -outfile ${OUTPUT_DIR}/ehet.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_het_uni/ -outfile ${OUTPUT_DIR}/eheu.csv


java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_hom_none/ -outfile ${OUTPUT_DIR}/ehon.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_hom_broad/ -outfile ${OUTPUT_DIR}/ehob.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_hom_trail/ -outfile ${OUTPUT_DIR}/ehot.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/emb_hom_uni/ -outfile ${OUTPUT_DIR}/ehou.csv



# ALIFE MODEL

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_het_none/ -outfile ${OUTPUT_DIR}/ahen.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_het_broad/ -outfile ${OUTPUT_DIR}/aheb.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_het_trail/ -outfile ${OUTPUT_DIR}/ahet.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_het_uni/ -outfile ${OUTPUT_DIR}/aheu.csv


java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_hom_none/ -outfile ${OUTPUT_DIR}/ahon.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_hom_broad/ -outfile ${OUTPUT_DIR}/ahob.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_hom_trail/ -outfile ${OUTPUT_DIR}/ahot.csv

java  -cp "../lib/commons-cli-1.2.jar:../lib/commons-math3-3.0.jar:../lib/mason17_mod.jar:." com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/alife_hom_uni/ -outfile ${OUTPUT_DIR}/ahou.csv
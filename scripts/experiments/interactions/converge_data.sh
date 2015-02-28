#!/bin/sh

SYNTH_BIN_DIR=/Users/sadat/synthscape/client
DATA_DIR=/Users/sadat/data
OUTPUT_DIR=/Users/sadat/convergence

# create OUTPUT_DIR if it doesn't exist
if [ ! -d "${OUTPUT_DIR}" ]; then
   mkdir ${OUTPUT_DIR}
fi

# clear everything that exists in the OUTPUT_DIR
rm -rf ${OUTPUT_DIR}/*

cd ${SYNTH_BIN_DIR}


#homogenous case

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_broad_100/ -outfile ${OUTPUT_DIR}/hom_broad_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_broad_75/ -outfile ${OUTPUT_DIR}/hom_broad_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_broad_50/ -outfile ${OUTPUT_DIR}/hom_broad_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_broad_25/ -outfile ${OUTPUT_DIR}/hom_broad_25.csv


java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_trail_100/ -outfile ${OUTPUT_DIR}/hom_trail_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_trail_75/ -outfile ${OUTPUT_DIR}/hom_trail_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_trail_50/ -outfile ${OUTPUT_DIR}/hom_trail_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_trail_25/ -outfile ${OUTPUT_DIR}/hom_trail_25.csv

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_uni_100/ -outfile ${OUTPUT_DIR}/hom_uni_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_uni_75/ -outfile ${OUTPUT_DIR}/hom_uni_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_uni_50/ -outfile ${OUTPUT_DIR}/hom_uni_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/hom_uni_25/ -outfile ${OUTPUT_DIR}/hom_uni_25.csv


#heterogenous case

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_100/ -outfile ${OUTPUT_DIR}/het_broad_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_75/ -outfile ${OUTPUT_DIR}/het_broad_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_50/ -outfile ${OUTPUT_DIR}/het_broad_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_25/ -outfile ${OUTPUT_DIR}/het_broad_25.csv


java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_trail_100/ -outfile ${OUTPUT_DIR}/het_trail_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_trail_75/ -outfile ${OUTPUT_DIR}/het_trail_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_trail_50/ -outfile ${OUTPUT_DIR}/het_trail_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_trail_25/ -outfile ${OUTPUT_DIR}/het_trail_25.csv

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_100/ -outfile ${OUTPUT_DIR}/het_uni_100.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_75/ -outfile ${OUTPUT_DIR}/het_uni_75.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_50/ -outfile ${OUTPUT_DIR}/het_uni_50.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_25/ -outfile ${OUTPUT_DIR}/het_uni_25.csv


#content

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_1s/ -outfile ${OUTPUT_DIR}/het_broad_1s.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_2s/ -outfile ${OUTPUT_DIR}/het_broad_2s.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_broad_3s/ -outfile ${OUTPUT_DIR}/het_broad_3s.csv

java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_1s/ -outfile ${OUTPUT_DIR}/het_uni_1s.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_2s/ -outfile ${OUTPUT_DIR}/het_uni_2s.csv
java  -cp "./synthscape.jar" com.synthverse.synthscape.analysis.Analyzer -dir ${DATA_DIR}/het_uni_3s/ -outfile ${OUTPUT_DIR}/het_uni_3s.csv


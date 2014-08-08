#!/bin/sh


function sim {
    java -jar  -Xms2048M -Xmx2048M -server client/synthscape.jar -job_name $1 -model $2 -species $3 -interactions $4 -data_dir $5
}

# First run all the basic tests
./compile

sim "isl_het_none" "island" "detector,extractor,transporter" "none" "/tmp"
sim "isl_het_trail" "island" "detector,extractor,transporter" "trail" "/tmp"
sim "isl_het_broad" "island" "detector,extractor,transporter" "broadcast" "/tmp"
sim "isl_het_uni" "island" "detector,extractor,transporter" "unicast_n" "/tmp"

sim "isl_hom_none" "island" "homogenous" "none" "/tmp"
sim "isl_hom_trail" "island" "homogenous" "trail" "/tmp"
sim "isl_hom_broad" "island" "homogenous" "broadcast" "/tmp"
sim "isl_hom_uni" "island" "homogenous" "unicast_n" "/tmp"


sim "emb_het_none" "embodied" "detector,extractor,transporter" "none" "/tmp"
sim "emb_het_trail" "embodied" "detector,extractor,transporter" "trail" "/tmp"
sim "emb_het_broad" "embodied" "detector,extractor,transporter" "broadcast" "/tmp"
sim "emb_het_uni" "embodied" "detector,extractor,transporter" "unicast_n" "/tmp"

sim "emb_hom_none" "embodied" "homogenous" "none" "/tmp"
sim "emb_hom_trail" "embodied" "homogenous" "trail" "/tmp"
sim "emb_hom_broad" "embodied" "homogenous" "broadcast" "/tmp"
sim "emb_hom_uni" "embodied" "homogenous" "unicast_n" "/tmp"


sim "ali_het_none" "alife" "detector,extractor,transporter" "none" "/tmp"
sim "ali_het_trail" "alife" "detector,extractor,transporter" "trail" "/tmp"
sim "ali_het_broad" "alife" "detector,extractor,transporter" "broadcast" "/tmp"
sim "ali_het_uni" "alife" "detector,extractor,transporter" "unicast_n" "/tmp"

sim "ali_hom_none" "alife" "homogenous" "none" "/tmp"
sim "ali_hom_trail" "alife" "homogenous" "trail" "/tmp"
sim "ali_hom_broad" "alife" "homogenous" "broadcast" "/tmp"
sim "ali_hom_uni" "alife" "homogenous" "unicast_n" "/tmp"









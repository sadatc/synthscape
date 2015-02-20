#!/bin/sh
ant/bin/ant jar
rm -rf client/scripts
cp -R scripts client
tar -cvf client.tar client
gzip client.tar
scp client.tar.gz s.chowdhury@chizen.csi.cuny.edu:

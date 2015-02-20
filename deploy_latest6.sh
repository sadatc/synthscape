#!/bin/sh
ant/bin/ant -f build6.xml jar
rm -rf client6/scripts
cp -R scripts client6
tar -cvf client6.tar client6
gzip client6.tar
scp client6.tar.gz s.chowdhury@chizen.csi.cuny.edu:
rm client6.tar.gz
rm -rf client6


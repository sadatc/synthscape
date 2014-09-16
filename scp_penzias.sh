#!/bin/sh
# create new jar and put it into penzias
rm -f client/synthscape.jar
ant/bin/ant jar
scp -r client/synthscape.jar s.chowdhury@penzias:
scp -r qsub_.sh s.chowdhury@penzias:
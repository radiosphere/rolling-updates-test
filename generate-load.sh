#!/bin/sh

# Use by passing a list of ports to command, the command will then start generating load to each of there ports.
while true 
do
    for port in "$@"
    do
        time -f %E curl -XPOST localhost:$port/session-bulk/create/200
        sleep 5
    done 
done


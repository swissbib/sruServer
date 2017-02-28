#!/bin/sh
#export JPDA_OPTS="-agentlib:jdwp=transport=dt_socket, address=1043, server=y, suspend=n"

export JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,address=39512,suspend=n,server=y"

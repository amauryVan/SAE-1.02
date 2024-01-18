#!/bin/bash
export CLASSPATH=`find ./lib -name "*.jar" | tr '\n' ':'`
export MAINCLASS=Minhistoria
java -cp ${CLASSPATH}:classes $MAINCLASS


#!/bin/bash

JAVA_HOME=/opt/jdk1.8.0_131

case $1 in
   start)
      nohup $JAVA_HOME/bin/java -jar data-simulate-1.0.0.jar >/dev/null 2>&1 &
     ;;
   stop)
     ps -ef | grep data-simulate-1.0.0.jar | grep -v grep|awk '{print $2}' | xargs kill
     ;;
esac
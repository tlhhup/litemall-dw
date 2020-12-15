#!/bin/bash

JAVA_HOME=/opt/jdk1.8.0_131

case $1 in
   start)
      nohup $JAVA_HOME/bin/java -jar litemall-wx-api-0.1.0-exec.jar >/dev/null 2>&1 &
     ;;
   stop)
     ps -ef | grep litemall-wx-api-0.1.0-exec.jar | grep -v grep|awk '{print $2}' | xargs kill
     ;;
esac
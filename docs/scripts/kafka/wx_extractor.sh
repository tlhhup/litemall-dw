#!/bin/bash

JAVA_HOME=/opt/jdk1.8.0_131
JAR_DIR=/home/hadoop/project/litemall/kafka
APP=kafka-extractor-1.0.0.jar

case $1 in
   start)
      nohup ${JAVA_HOME}/bin/java -cp ${JAR_DIR}/${APP} org.tlh.dw.LitemallWxExtractor >/dev/null 2>&1 &
     ;;
   stop)
     ps -ef | grep kafka-extractor-1.0.0.jar | grep -v grep|awk '{print $2}' | xargs kill
     ;;
esac
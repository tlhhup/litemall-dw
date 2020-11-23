#!/bin/bash

FLUME_HOME=/opt/apache-flume-1.9.0-bin

case $1 in
   start)
      nohup $FLUME_HOME/bin/flume-ng agent -c conf -f $FLUME_HOME/conf/litemall_kafka_to_hdfs.conf --name a1 -Dflume.root.logger=INFO,LOGFILE >$FLUME_HOME/logs/litemall 2>&1 &
     ;;
   stop)
     ps -ef | grep litemall_kafka_to_hdfs | grep -v grep|awk '{print $2}' | xargs kill
     ;;
esac

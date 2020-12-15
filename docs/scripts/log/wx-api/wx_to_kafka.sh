#!/bin/bash

FLUME_HOME=/opt/apache-flume-1.9.0-bin

case $1 in
   start)
      nohup $FLUME_HOME/bin/flume-ng agent -c $FLUME_HOME/conf -f $FLUME_HOME/conf/litemall_wx_log_to_kafka.conf --name a1 -Dflume.root.logger=INFO,console >$FLUME_HOME/logs/litemall-wx 2>&1 &
     ;;
   stop)
     ps -ef | grep litemall_wx_log_to_kafka | grep -v grep|awk '{print $2}' | xargs kill
     ;;
esac
#!/bin/bash

MAXWELL_HOME=/home/hadoop/project/litemall/maxwell

case $1 in
    start)
        nohup $MAXWELL_HOME/bin/maxwell --config $MAXWELL_HOME/config.properties >/dev/null 2>&1 &
    ;;
    stop)
        ps -ef | grep maxwell | grep -v grep|awk '{print $2}'|xargs kill
    ;;
esac

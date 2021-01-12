#!/bin/bash

SPARK_HOME=/opt/spark-3.0.0
JAR_DIR=/home/hadoop/project/litemall/spark
APP=litemall-real-time-1.0.0.jar

$SPARK_HOME/bin/spark-submit \
  --class org.tlh.dw.DwRealTimeDriver \
  --master spark://hadoop-master:7077 \
  --executor-memory 2G \
  --total-executor-cores 2 \
  $JAR_DIR/$APP \
  5
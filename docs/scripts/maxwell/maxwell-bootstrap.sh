#!/bin/bash

MAXWELL_HOME=/home/hadoop/project/litemall/maxwell

$MAXWELL_HOME/bin/maxwell-bootstrap --config $MAXWELL_HOME/config.properties --client_id client_1 --database litemall --table $1
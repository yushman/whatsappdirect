#!/bin/sh

SERVICE_NAME=WhatsAppDirect
PATH_TO_JAR=./build/distributions/whatsappdirect-0.1/bin/whatsappdirect
PID_PATH_NAME=/tmp/$SERVICE_NAME-pid

echo "Starting $SERVICE_NAME ..."
if [ ! -f $PID_PATH_NAME ]; then
    nohup $PATH_TO_JAR >> log.txt 2>&1&
    echo $! > $PID_PATH_NAME
    echo "$SERVICE_NAME started ..."
else
    echo "$SERVICE_NAME is already running ..."
fi

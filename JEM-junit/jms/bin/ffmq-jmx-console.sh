#!/bin/sh

CLASSPATH=../lib/commons-logging-1.1.jar:../lib/log4j-1.2.15.jar:../lib/jms-1.1.jar
CLASSPATH=${CLASSPATH}:../lib/ffmq3-core.jar:../lib/ffmq3-server.jar

java -Xmx64m -cp "${CLASSPATH}" net.timewalker.ffmq3.FFMQJMXConsoleLauncher $*
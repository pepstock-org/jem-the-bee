@echo off

set CLASSPATH=../lib/commons-logging-1.1.jar;../lib/log4j-1.2.15.jar;../lib/jms-1.1.jar;../lib/mx4j-3.0.2.jar;../lib/mx4j-remote-3.0.2.jar
set CLASSPATH=%CLASSPATH%;../lib/ffmq3-core.jar;../lib/ffmq3-server.jar

java -Xmx256m -cp "%CLASSPATH%" net.timewalker.ffmq3.FFMQServerLauncher
@echo off
setlocal

call %JEM_HOME%/bin/jem_set_classpath.cmd

java -cp %CLASSPATH% org.pepstock.jem.util.migrate.DBMaint %1 %2

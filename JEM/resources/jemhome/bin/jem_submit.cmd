@echo off
setlocal
call %JEM_HOME%/bin/jem_set_classpath.cmd

set JAVA_OPT=
set PGM_OPT=

:checkParms
if [%1]==[] goto :continue

set OPTION=%~1
if %OPTION:~0,2%==-D (
   set JAVA_OPT=%JAVA_OPT% %OPTION%=%~2
   shift
) else (
   set PGM_OPT=%PGM_OPT% %OPTION%
)
shift
goto :checkParms

:continue
java -cp %CLASSPATH% %JAVA_OPT% org.pepstock.jem.commands.Submit %PGM_OPT%

@echo off
setlocal

:checkParms
if [%1]==[] goto :continue

set OPTION=%~1
set PGM_OPT=%PGM_OPT% %OPTION%
shift
goto :checkParms

:continue
node %JEM_HOME%\bin\jem_submit.js %PGM_OPT%

# !/bin/bash

for OPTION in "$@"
do
   PGM_OPT="$PGM_OPT $OPTION"
done

node $JEM_HOME\bin\jem_submit.js $PGM_OPT

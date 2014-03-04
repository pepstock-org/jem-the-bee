# !/bin/bash

source $JEM_HOME/bin/jem_set_classpath.sh

java -cp $CLASSPATH org.pepstock.jem.commands.LocalHostSubmit $*

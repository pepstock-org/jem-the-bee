# !/bin/bash
source $JEM_HOME/bin/jem_set_classpath.sh

for OPTION in "$@"
do
   if [ ${OPTION:0:2} = -D ] ; then
      JAVA_OPT="$JAVA_OPT $OPTION"
   else
      PGM_OPT="$PGM_OPT $OPTION"
   fi
done

java -cp $CLASSPATH $JAVA_OPT org.pepstock.jem.commands.ProxySubmit $PGM_OPT

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

java -cp $CLASSPATH $JAVA_OPT org.pepstock.jem.commands.docker.StartUpWeb $PGM_OPT
if [ $? -ne 0 ]; then
    exit 1
fi

cp /mnt/jem/persistence/$JEM_ENVIRONMENT/web/jem_gwt.war /usr/local/tomcat/webapps
catalina.sh run
# !/bin/bash
#----------------------------------------------
# Start up of JEM web inside a container
#----------------------------------------------
#
# import the classpath
source $JEM_HOME/bin/jem_set_classpath.sh

#
# Run the configurator which creates the webapp
# using the environment variable needed, JEM_ENVIROMENT.
# if configurator has got some issues, close in RC 1
java -cp $CLASSPATH org.pepstock.jem.commands.docker.StartUpWeb
if [ $? -ne 0 ]; then
    exit 1
fi

#
# set the persistence path, where webapp is located
JEM_PERSISTENCE=/mnt/jem/persistence

#
# copy the war file, which contains the webapp, into TOMCAT webapp folder
cp $JEM_PERSISTENCE/$JEM_ENVIRONMENT/web/jem_gwt.war /usr/local/tomcat/webapps

#
# Run TOMCAT
# it uses "exec" to maintain the PID 1 inside of container
# so that it can receive teh SIGTERM when docker stops the container
exec catalina.sh run
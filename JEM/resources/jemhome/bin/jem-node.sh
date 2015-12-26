# !/bin/bash
#----------------------------------------------
# Start up of JEM node inside a container
#----------------------------------------------
#
# import the classpath
source $JEM_HOME/bin/jem_set_classpath.sh

#
# Run the configurator which creates a standard configuration
# using the environment variables needed, like JEM_ENVIROMENT,
# JEM_DOMAIN, JEM_AFFINITY.
# At this point JEM_ENVIRONMENT (only mandatory variable) contains
# ONLY the name of environment. Afterwards it will be changed 
# with the path of the environment configuration
# if configurator has got some issues, close in RC 1
java -cp $CLASSPATH org.pepstock.jem.commands.docker.StartUpNode
if [ $? -ne 0 ]; then
    exit 1
fi

#
# set the persistence path, where config is located
JEM_PERSISTENCE=/mnt/jem/persistence

#
# save the name of the environment into a new variable
# and override the JEM_ENVIRONMENT with the path of the environment configuration
# It uses EXPORT because the variable must be read by the node which will be executed
# with the "exec" BASH command
JEM_ENVIRONMENT_NAME=$JEM_ENVIRONMENT
export JEM_ENVIRONMENT=$JEM_HOME/$JEM_ENVIRONMENT

#
# set the node variables. The node name is always the default
# therefore "node-000" and set the path where the node configuration is located
# It uses EXPORT because the variable must be read by the node which will be executed
# with the "exec" BASH command
JEM_NODE_NAME=node-000
export JEM_NODE=$JEM_ENVIRONMENT/$JEM_NODE_NAME

#
# set all JAVA options and add the configuration files for log4j, JEM (nodes and env) and
# Hazelcast
JAVA_OPT="-Xincgc"
JAVA_OPT="$JAVA_OPT -Xms128m"
JAVA_OPT="$JAVA_OPT -Xmx512m"
JAVA_OPT="$JAVA_OPT -Dlog4j.config=$JEM_PERSISTENCE/$JEM_ENVIRONMENT_NAME/config/log4j.xml"
JAVA_OPT="$JAVA_OPT -Djem.config=$JEM_NODE/config/jem-node.xml"
JAVA_OPT="$JAVA_OPT -Djem.env=$JEM_PERSISTENCE/$JEM_ENVIRONMENT_NAME/config/jem-env.xml"
JAVA_OPT="$JAVA_OPT -Dhazelcast.config=$JEM_PERSISTENCE/$JEM_ENVIRONMENT_NAME/config/jem-env-hazelcast.xml"

#
# Reduce the visibility of persistence to avoid that some batches try to access
chmod -R 700 $JEM_PERSISTENCE

#
# Run the main program in JAVA of JEM node
# it uses "exec" to maintain the PID 1 inside of container
# so that it can receive teh SIGTERM when docker stops the container
exec java -cp $CLASSPATH $JAVA_OPT org.pepstock.jem.node.Main
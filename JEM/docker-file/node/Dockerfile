###
# Docker file to create JEM node
# deploying it on JAVA
###
FROM java:7-jre
MAINTAINER Andrea Stock Stocchero <info@pepstock.org>
###
# Creates the JEM folder
###
RUN mkdir /opt/jem && \
    chmod -R 777 /opt/jem && \
    groupadd -f jem && \
    useradd -m -d /opt/jem -g jem jem && \
    mkdir /mnt/jem && \
    chown -R jem:jem /mnt/jem && \
    chmod -R 770 /mnt/jem
###
# Swtich to 'jem' user
###
USER jem
###
# Copies JEM.zip distribution file fo /opt/jem
# and unzip it
###
COPY jem.zip /opt/jem
WORKDIR /opt/jem
RUN unzip jem && \
    chmod -R 770 jemhome && \
    chmod -R 777 jemhome/bin && \
    rm jem.zip
###
# Environment variables
###
ENV JEM_HOME=/opt/jem/jemhome
ENV PATH $JEM_HOME/bin:$PATH
###
# Expose the ports which are necessary>
# 5710 - Hazelcast cluster
# 54327 - Hazelcast multicast group
# 5910 - JEM HTTPS listener
###
##EXPOSE 5710 54327 5910
###
# Standard command: BASH
###
CMD ["/bin/bash"]
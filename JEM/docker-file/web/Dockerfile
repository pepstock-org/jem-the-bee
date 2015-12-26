###
# Docker file to create JEM web app
# deploying it on TOMCAT
###
FROM tomcat:7
MAINTAINER Andrea Stock Stocchero <info@pepstock.org>
###
# Creates the JEM folder
###
RUN mkdir /opt/jem && \
    chmod -R 777 /opt/jem && \
    mkdir /mnt/jem && \
    chmod -R 770 /mnt/jem
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

EXPOSE 9701
###
# Start of JEM installation and TOMCAT startup
###
CMD ["/bin/bash", "jem-web.sh"]
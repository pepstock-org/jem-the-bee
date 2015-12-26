###
# Docker file to create JEM database 
# deploying it on MYSQL
###
FROM mysql
MAINTAINER Andrea Stock Stocchero <info@pepstock.org>
###
# Copies alter database to change the collate
# of default database
# and unzip it
###
COPY ALTER_DATABASE.sql /docker-entrypoint-initdb.d

###
# Environment variables
###
ENV MYSQL_DATABASE=jem
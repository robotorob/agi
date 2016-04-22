#!/bin/bash


######################################################################
# Use this 'variables.sh' if you are running the system in a 
# Docker container.  
# BEWARE: Some of the parameters NEED to be the same inside and outside the container 
# (such as API_PORT, depending on the port mappings).
######################################################################


# ----------------------------------------
# AGI Home 
# ---------------------------------------
export AGI_HOME=~/dev/agi


# ----------------------------------------
# Dependencies
# ---------------------------------------

# POSTGRESQL
export POSTGRESQL_BIN=/usr/bin/psql

# MAVEN
export MAVEN_BIN=/usr/share/maven/bin/mvn

# Java
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64


# ----------------------------------------
# Set Path
# ----------------------------------------
export PATH=${MAVEN_HOME}/bin:${PATH}
export PATH=${JAVA_HOME}/bin:${PATH}

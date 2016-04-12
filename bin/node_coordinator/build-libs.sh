#!/bin/bash

variables_file=${VARIABLES_FILE:-"variables.sh"}
echo "Using variables file = \"$variables_file\""
source $(dirname $0)/../$variables_file


echo "------- build all the library dependencies of the experimental-framework -------"
cd ../../../algorithms/code/core
$MAVEN_BIN clean install
